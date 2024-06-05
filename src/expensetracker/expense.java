/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package expensetracker;

import com.toedter.calendar.JDateChooser;
import java.sql.*;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/**
 *
 * @author Prim
 */
public class expense extends javax.swing.JFrame {

    Connection con;
    PreparedStatement stm;

    Login_page login = new Login_page();
    static String next = "";

    public expense() {
        initComponents();
        Connect();
        updateCombo();
        login.info(next);
        
     desc.setLineWrap(true);
        desc.setWrapStyleWord(true);

    }
    public String User_ID = login.userId;

    public void Connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/expensetracker_db", "root", "");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Income.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Income.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void updateCombo() {
        String sql = "SELECT * FROM category_table WHERE CategoryType= 'Expense'";
        try {
            stm = con.prepareStatement(sql);
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                expensecat.addItem(rs.getString("CategoryName"));

            }
        } catch (SQLException ex) {
            Logger.getLogger(Income.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     private boolean isValidAmount(String amount) {
        try {
            Double.parseDouble(amount);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
     }
      private String wrapDescription(String description, int lineLength) {
        StringBuilder wrappedDescription = new StringBuilder(description);

        int i = 0;
        while (i + lineLength < wrappedDescription.length() && (i = wrappedDescription.lastIndexOf(" ", i + lineLength)) != -1) {
            wrappedDescription.replace(i, i + 1, "\n");
        }
        return wrappedDescription.toString();
    }
      void submitbutton(){
          String Category, Description, Date;
        String Amount;

        String url = "jdbc:MySQL://localhost:3306/expensetracker_db";
        String suser = "root";
        String spass = "";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, suser, spass);

            if ("".equals(jdate.getDateFormatString()) || jdate.getDate() == null) {
                JOptionPane.showMessageDialog(new JFrame(), "Date is required!", "Error!", JOptionPane.WARNING_MESSAGE);

            }else if(!isValidAmount(amount.getText())){
                JOptionPane.showMessageDialog(new JFrame(), "Invalid Amount!", "Error!", JOptionPane.WARNING_MESSAGE);
                
            }else if ("".equals(amount.getText())) {
                JOptionPane.showMessageDialog(new JFrame(), "Amount is required!", "Error!", JOptionPane.WARNING_MESSAGE);
            } else if ("".equals(desc.getText())) {
                JOptionPane.showMessageDialog(new JFrame(), "Description is required!", "Error!", JOptionPane.WARNING_MESSAGE);
            } else if (expensecat.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(new JFrame(), "Category is required!", "Error!", JOptionPane.WARNING_MESSAGE);
            } else {

                Description = wrapDescription(desc.getText(), 40);
                JDateChooser jDateChooser = (JDateChooser) jdate;
                java.util.Date selectedDate = jDateChooser.getDate();
                java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime()); // Convert util.Date to sql.Date

                Amount = amount.getText();
                Category = expensecat.getSelectedItem().toString();
                double expenseAmount = Double.parseDouble(Amount);
                String getBalanceQuery = "SELECT BalanceID, BalanceAmount FROM balance_table WHERE UserID = ?";
                PreparedStatement getBalanceStmt = con.prepareStatement(getBalanceQuery);
                getBalanceStmt.setString(1, User_ID);
                ResultSet rs = getBalanceStmt.executeQuery();

                int BalanceID = -1;
                double currentBalance = 0.0;
                if (rs.next()) {
                    BalanceID = rs.getInt("BalanceID");
                    currentBalance = rs.getDouble("BalanceAmount");
                } else {
                    JOptionPane.showMessageDialog(new JFrame(), "Balance not Enough! Check your balance!", "Error!", JOptionPane.WARNING_MESSAGE);
                    con.rollback();
                    return;
                }

                double newBalance = currentBalance - expenseAmount;

                String updateBalanceQuery = "UPDATE balance_table SET BalanceAmount = ? WHERE BalanceID = ?";
                PreparedStatement updateBalanceStmt = con.prepareStatement(updateBalanceQuery);
                updateBalanceStmt.setDouble(1, newBalance);
                updateBalanceStmt.setInt(2, BalanceID);
                updateBalanceStmt.executeUpdate();

                String expenseQuery = "INSERT INTO expense_table (UserID, CategoryID, BalanceID, Amount, Description, Date) "
                        + "VALUES (?, (SELECT CategoryID FROM category_table WHERE CategoryType = 'Expense' AND CategoryName = ?), ?, ?, ?, ?)";
                PreparedStatement expenseStmt = con.prepareStatement(expenseQuery, Statement.RETURN_GENERATED_KEYS);
                expenseStmt.setString(1, User_ID);
                expenseStmt.setString(2, Category);
                expenseStmt.setInt(3, BalanceID);
                expenseStmt.setDouble(4, expenseAmount);
                expenseStmt.setString(5, Description);
                expenseStmt.setDate(6, sqlDate);
                expenseStmt.executeUpdate();

                ResultSet expenseGeneratedKeys = expenseStmt.getGeneratedKeys();
                int ExpenseID = -1;
                if (expenseGeneratedKeys.next()) {
                    ExpenseID = expenseGeneratedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating expense record failed, no ID obtained.");
                }

                String transactionQuery = "INSERT INTO transaction_table (UserID, BalanceID, ExpenseID) VALUES (?, ?, ?)";
                PreparedStatement transactionStmt = con.prepareStatement(transactionQuery);
                transactionStmt.setString(1, User_ID);
                transactionStmt.setInt(2, BalanceID);
                transactionStmt.setInt(3, ExpenseID);
                transactionStmt.executeUpdate();

                desc.setText("");
                amount.setText("");
                expensecat.setSelectedItem("");

                JOptionPane.showMessageDialog(this, "Added!");
                
                con.close();
                
                new ET_Home().setVisible(true);
                this.dispose();
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(expense.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(expense.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        amount = new javax.swing.JTextField();
        expensecat = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jdate = new com.toedter.calendar.JDateChooser();
        jScrollPane1 = new javax.swing.JScrollPane();
        desc = new javax.swing.JTextArea();
        button1 = new swing.Button();
        button2 = new swing.Button();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 224, 224));

        jLabel1.setFont(new java.awt.Font("Lucida Fax", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(173, 128, 171));
        jLabel1.setText("Input Expense");

        jLabel2.setFont(new java.awt.Font("Lucida Fax", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(173, 128, 171));
        jLabel2.setText("Category");

        jLabel3.setFont(new java.awt.Font("Lucida Fax", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(173, 128, 171));
        jLabel3.setText("Amount:");

        jLabel4.setFont(new java.awt.Font("Lucida Fax", 1, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(173, 128, 171));
        jLabel4.setText("Description");

        amount.setFont(new java.awt.Font("Lucida Fax", 0, 12)); // NOI18N
        amount.setForeground(new java.awt.Color(173, 85, 146));
        amount.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(153, 0, 153), new java.awt.Color(102, 0, 102)));

        expensecat.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(173, 85, 146), 2, true));
        expensecat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                expensecatActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Lucida Fax", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(173, 128, 171));
        jLabel5.setText("Date");

        jdate.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(173, 85, 146), 2, true));
        jdate.setForeground(new java.awt.Color(173, 85, 146));

        desc.setColumns(20);
        desc.setFont(new java.awt.Font("Lucida Fax", 0, 12)); // NOI18N
        desc.setForeground(new java.awt.Color(173, 85, 146));
        desc.setRows(5);
        desc.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(173, 85, 146), 2, true));
        jScrollPane1.setViewportView(desc);

        button1.setBackground(new java.awt.Color(173, 128, 171));
        button1.setText("Submit");
        button1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button1ActionPerformed(evt);
            }
        });

        button2.setBackground(new java.awt.Color(173, 128, 171));
        button2.setText("Cancel");
        button2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(118, 118, 118))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(48, 48, 48)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(amount, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(expensecat, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jdate, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(button1, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(button2, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(57, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(expensecat, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(amount, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jdate, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void expensecatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_expensecatActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_expensecatActionPerformed

    private void button1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button1ActionPerformed
        // TODO add your handling code here:
        submitbutton();
    }//GEN-LAST:event_button1ActionPerformed

    private void button2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button2ActionPerformed
        // TODO add your handling code here:
        new ET_Home().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_button2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new expense().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField amount;
    private swing.Button button1;
    private swing.Button button2;
    private javax.swing.JTextArea desc;
    private javax.swing.JComboBox<String> expensecat;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private com.toedter.calendar.JDateChooser jdate;
    // End of variables declaration//GEN-END:variables
}
