package expensetracker;

import expensetracker.Login_page;
import com.toedter.calendar.JDateChooser;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
/**
 *
 * @author Prim
 */
public class Income extends javax.swing.JFrame {

    Connection con;
    PreparedStatement stm;
    Login_page login = new Login_page();
    static String next = "";

    public Income() {
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
        String sql = "SELECT * FROM category_table WHERE CategoryType= 'Income'";
        try {
            stm = con.prepareStatement(sql);
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                incomcat.addItem(rs.getString("CategoryName"));

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
            }else if ("".equals(desc.getText())) {
                JOptionPane.showMessageDialog(new JFrame(), "Description is required!", "Error!", JOptionPane.WARNING_MESSAGE);
            }else if (incomcat.getSelectedItem()== null) {
                JOptionPane.showMessageDialog(new JFrame(), "Category is required!", "Error!", JOptionPane.WARNING_MESSAGE);
            } else {
                Description = wrapDescription(desc.getText(), 40);
                JDateChooser jDateChooser = (JDateChooser) jdate;
                java.util.Date selectedDate = jDateChooser.getDate();
                java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime()); // Convert util.Date to sql.Date

                Amount = amount.getText();
                Category = incomcat.getSelectedItem().toString();
                double newAmount = Double.parseDouble(Amount);
                String checkBalanceQuery = "SELECT BalanceID, BalanceAmount FROM balance_table WHERE UserID = ?";
                PreparedStatement checkBalanceStmt = con.prepareStatement(checkBalanceQuery);
                checkBalanceStmt.setString(1, User_ID);
                ResultSet rs = checkBalanceStmt.executeQuery();

                int BalanceID = -1;
                if (rs.next()) {

                    BalanceID = rs.getInt("BalanceID");
                    double currentBalance = rs.getDouble("BalanceAmount");
                    double updatedBalance = currentBalance + newAmount;

                    String updateBalanceQuery = "UPDATE balance_table SET BalanceAmount = ? WHERE BalanceID = ?";
                    PreparedStatement updateBalanceStmt = con.prepareStatement(updateBalanceQuery);
                    updateBalanceStmt.setDouble(1, updatedBalance);
                    updateBalanceStmt.setInt(2, BalanceID);
                    updateBalanceStmt.executeUpdate();
                } else {
                    String insertBalanceQuery = "INSERT INTO balance_table (UserID, BalanceAmount) VALUES (?, ?)";
                    PreparedStatement insertBalanceStmt = con.prepareStatement(insertBalanceQuery, Statement.RETURN_GENERATED_KEYS);
                    insertBalanceStmt.setString(1, User_ID);
                    insertBalanceStmt.setDouble(2, newAmount);
                    insertBalanceStmt.executeUpdate();

                    ResultSet generatedKeys = insertBalanceStmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        BalanceID = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Creating balance record failed, no ID obtained.");
                    }

                }

                String incomeQuery = "INSERT INTO income_table (UserID, CategoryID, BalanceID, Amount, Description, Date) VALUES (?, (SELECT CategoryID FROM category_table WHERE CategoryType = 'Income' AND CategoryName = ?), ?, ?, ?, ?)";
                PreparedStatement incomeStmt = con.prepareStatement(incomeQuery, Statement.RETURN_GENERATED_KEYS);
                incomeStmt.setString(1, User_ID);
                incomeStmt.setString(2, Category);
                incomeStmt.setInt(3, BalanceID);
                incomeStmt.setDouble(4, newAmount);
                incomeStmt.setString(5, Description);
                incomeStmt.setDate(6, sqlDate);
                incomeStmt.executeUpdate();

                ResultSet incomeGeneratedKeys = incomeStmt.getGeneratedKeys();
                int IncomeID = -1;
                if (incomeGeneratedKeys.next()) {
                    IncomeID = incomeGeneratedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating income record failed, no ID obtained.");
                }

                String transactionQuery = "INSERT INTO transaction_table (UserID, BalanceID, IncomeID) VALUES (?, ?, ?)";
                PreparedStatement transactionStmt = con.prepareStatement(transactionQuery);
                transactionStmt.setString(1, User_ID);
                transactionStmt.setInt(2, BalanceID);
                transactionStmt.setInt(3, IncomeID);
                transactionStmt.executeUpdate();

                desc.setText("");
                amount.setText("");
                incomcat.setSelectedItem("");

                JOptionPane.showMessageDialog(this, "Added!");
                con.close();
                new ET_Home().setVisible(true);
                this.dispose();
                
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Income.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Income.class.getName()).log(Level.SEVERE, null, ex);
        }
     }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        amount = new javax.swing.JTextField();
        incomcat = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jdate = new com.toedter.calendar.JDateChooser();
        jScrollPane1 = new javax.swing.JScrollPane();
        desc = new javax.swing.JTextArea();
        button1 = new swing.Button();
        button2 = new swing.Button();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 224, 224));
        jPanel1.setForeground(new java.awt.Color(173, 85, 146));

        jLabel1.setFont(new java.awt.Font("Lucida Fax", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(173, 128, 171));
        jLabel1.setText("Input Income");

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
        amount.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(173, 85, 146), 2, true));

        incomcat.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(173, 85, 146), 2, true));
        incomcat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                incomcatActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Lucida Fax", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(173, 128, 171));
        jLabel5.setText("Date");

        jdate.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(173, 85, 146), 2, true));
        jdate.setForeground(new java.awt.Color(173, 85, 146));

        desc.setColumns(20);
        desc.setForeground(new java.awt.Color(173, 85, 146));
        desc.setRows(5);
        desc.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(173, 85, 146), 2, true));
        jScrollPane1.setViewportView(desc);

        button1.setBackground(new java.awt.Color(173, 128, 171));
        button1.setText("Cancel");
        button1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button1ActionPerformed(evt);
            }
        });

        button2.setBackground(new java.awt.Color(173, 128, 171));
        button2.setText("Submit");
        button2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addComponent(jLabel1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(51, 51, 51)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(button2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(43, 43, 43)
                                        .addComponent(button1, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(incomcat, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel4)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jdate, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(amount, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(jScrollPane1)))))))
                .addContainerGap(51, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel1)
                .addGap(26, 26, 26)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(incomcat, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(amount, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jdate, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button2, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(48, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void incomcatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_incomcatActionPerformed


    }//GEN-LAST:event_incomcatActionPerformed

    private void button2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button2ActionPerformed
        // TODO add your handling code here:
        submitbutton();
    }//GEN-LAST:event_button2ActionPerformed

    private void button1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button1ActionPerformed
        // TODO add your handling code here:
        new ET_Home().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_button1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
       
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Income().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField amount;
    private swing.Button button1;
    private swing.Button button2;
    private javax.swing.JTextArea desc;
    private javax.swing.JComboBox<String> incomcat;
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
