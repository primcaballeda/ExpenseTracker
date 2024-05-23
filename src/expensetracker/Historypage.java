package expensetracker;

import com.toedter.calendar.IDateEditor;
import expensetracker.Login_page;
import javax.swing.JFrame;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.toedter.calendar.JDateChooser;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

public class Historypage extends javax.swing.JFrame {

    Login_page login = new Login_page();
    static String next = "";

    public Historypage() {
        initComponents();
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        jtable.getTableHeader().setBackground(new Color(32, 106, 203));
        Connect();
        login.info(next);
        showtable();
        catcmb();
        ExTableCustom();

    }
    Connection con;
    PreparedStatement smt;
    public String User_ID = login.userId;

    private void ExTableCustom() {
        jtable.setFillsViewportHeight(true);
        jtable.setRowHeight(30);
        jtable.setShowGrid(false);
        jtable.setIntercellSpacing(new Dimension(0, 0));
        jtable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jtable.setSelectionBackground(new Color(255, 224, 224, 255));
        jtable.setSelectionForeground(new Color(173, 85, 146));
        jtable.setBackground(Color.GRAY);
        jtable.setBackground(Color.WHITE);
        jtable.setForeground(new Color(173, 85, 146));

        JTableHeader header = jtable.getTableHeader();
        header.setPreferredSize(new Dimension(header.getWidth(), 30));
        header.setBackground(new Color(255, 224, 224, 255));
        header.setForeground(Color.BLACK);
        header.setFont(header.getFont().deriveFont(Font.BOLD));
        jtable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(jtable, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, Color.WHITE));
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(jtable, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, Color.WHITE));
                setBackground(new Color(255, 224, 224, 255));
                setForeground(Color.BLACK);
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        };

        for (int i = 0; i < jtable.getColumnModel().getColumnCount(); i++) {
            jtable.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }
    }

    void Connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/expensetracker_db", "root", "");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Historypage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Historypage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void catcmb() {
        String sql = "SELECT CategoryName FROM category_table";
        try {
            smt = con.prepareStatement(sql);
            ResultSet rs = smt.executeQuery();

            while (rs.next()) {
                category.addItem(rs.getString("CategoryName"));

            }
        } catch (SQLException ex) {
            Logger.getLogger(Income.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static DefaultTableModel model;

    void showtable() {
        model = (DefaultTableModel) jtable.getModel();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/expensetracker_db", "root", "");

            String query = "SELECT t.ExpenseID, t.IncomeID, c.CategoryName, c.CategoryType, "
                    + "IFNULL(e.Amount, i.Amount) AS Amount, "
                    + "IFNULL(e.Description, i.Description) AS Description, "
                    + "IFNULL(e.Date, i.Date) AS Date "
                    + "FROM transaction_table t "
                    + "LEFT JOIN expense_table e ON t.ExpenseID = e.ExpenseID "
                    + "LEFT JOIN income_table i ON t.IncomeID = i.IncomeID "
                    + "LEFT JOIN category_table c ON (c.CategoryID = e.CategoryID OR c.CategoryID = i.CategoryID) "
                    + "WHERE t.UserID = ?";

            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, User_ID);

            ResultSet rs = stmt.executeQuery();

            model.setRowCount(0);

            while (rs.next()) {
                String expenseID = rs.getString("ExpenseID");
                String incomeID = rs.getString("IncomeID");

                String categoryName = rs.getString("CategoryName");
                String categoryType = rs.getString("CategoryType");
                double amount = rs.getDouble("Amount");
                String description = rs.getString("Description");
                Date date = rs.getDate("Date");

                model.addRow(new Object[]{categoryName, categoryType, amount, description, date, expenseID, incomeID});
            }
            jtable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    if (!e.getValueIsAdjusting()) {
                        int selectedRowIndex = jtable.getSelectedRow();
                        if (selectedRowIndex != -1) {
                            String categoryType = (String) model.getValueAt(selectedRowIndex, 1);

                            populateCategoryComboBox(categoryType);

                            category.setSelectedItem((String) model.getValueAt(selectedRowIndex, 0));
                            amount.setText(String.valueOf(model.getValueAt(selectedRowIndex, 2)));
                            desc.setText((String) model.getValueAt(selectedRowIndex, 3));
                            jdate.setDate((Date) model.getValueAt(selectedRowIndex, 4));

                            IncomeID.setText((String) model.getValueAt(selectedRowIndex, 5));
                            expenseID.setText((String) model.getValueAt(selectedRowIndex, 6));
                        }
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateCategoryComboBox(String categoryType) {
        List<String> categories = getCategoriesFromDatabase(categoryType);

        DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();
        for (String category : categories) {
            comboBoxModel.addElement(category);
        }
        category.setModel(comboBoxModel);
    }

    private List<String> getCategoriesFromDatabase(String categoryType) {
        List<String> categories = new ArrayList<>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/expensetracker_db", "root", "");

            String query = "SELECT CategoryName FROM category_table WHERE CategoryType = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, categoryType);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                categories.add(rs.getString("CategoryName"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return categories;
    }

    void updateTableRow() {
        int selectedRowIndex = jtable.getSelectedRow();
        if (selectedRowIndex != -1) {
            String categoryType = (String) model.getValueAt(selectedRowIndex, 1);
            String categoryName = (String) category.getSelectedItem();
            String selectedExpenseID = (String) model.getValueAt(selectedRowIndex, 5);
            String selectedIncomeID = (String) model.getValueAt(selectedRowIndex, 6);

            double amountValue = Double.parseDouble(amount.getText());
            String description = desc.getText();
            JDateChooser jDateChooser = (JDateChooser) jdate;
            java.util.Date selectedDate = jDateChooser.getDate();
            java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                con = DriverManager.getConnection("jdbc:mysql://localhost/expensetracker_db", "root", "");

                String tableName = "";
                String idColumnName = "";
                String amountColumnName = "";
                String descriptionColumnName = "";
                String dateColumnName = "";
                String categoryIDColumnName = "";
                if ("Expense".equals(categoryType)) {
                    tableName = "expense_table";
                    idColumnName = "ExpenseID";
                    amountColumnName = "Amount";
                    descriptionColumnName = "Description";
                    dateColumnName = "Date";
                    categoryIDColumnName = "CategoryID";
                } else if ("Income".equals(categoryType)) {
                    tableName = "income_table";
                    idColumnName = "IncomeID";
                    amountColumnName = "Amount";
                    descriptionColumnName = "Description";
                    dateColumnName = "Date";
                    categoryIDColumnName = "CategoryID";
                } else {
                    System.err.println("Unknown category type: " + categoryType);
                    return;
                }

                String updateQuery = "UPDATE " + tableName + " SET "
                        + amountColumnName + " = ?, "
                        + descriptionColumnName + " = ?, "
                        + dateColumnName + " = ?, "
                        + categoryIDColumnName + " = (SELECT CategoryID FROM category_table WHERE CategoryName = ? LIMIT 1) "
                        + "WHERE " + idColumnName + " = ?";
                PreparedStatement updateStmt = con.prepareStatement(updateQuery);
                updateStmt.setDouble(1, amountValue);
                updateStmt.setString(2, description);
                updateStmt.setDate(3, sqlDate);
                updateStmt.setString(4, categoryName);

                if ("Expense".equals(categoryType)) {
                    updateStmt.setString(5, selectedExpenseID);
                } else if ("Income".equals(categoryType)) {
                    updateStmt.setString(5, selectedIncomeID);
                }

                updateStmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Updated Successfully!");

                if ("Expense".equals(categoryType)) {
                    updateBalance(con, amountValue, false);
                } else if ("Income".equals(categoryType)) {
                    updateBalance(con, amountValue, true);
                }

                con.close();
                showtable();

            } catch (ClassNotFoundException | SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to update row: " + ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select a row to update");
        }
    }

    void updateBalance(Connection con, double newAmount, boolean isIncome) throws SQLException {

        String balanceFetchQuery = "SELECT SUM(Amount) FROM income_table WHERE UserID = ?";
        PreparedStatement balanceFetchStmt = con.prepareStatement(balanceFetchQuery);
        balanceFetchStmt.setString(1, User_ID);
        ResultSet balanceRs = balanceFetchStmt.executeQuery();
        double incomeAmount = 0.0;
        if (balanceRs.next()) {
            incomeAmount = balanceRs.getDouble(1);
        }

        double sumOfexpense = 0.0;
        String sumIncomeQuery = "SELECT SUM(Amount) FROM expense_table WHERE UserID = ?";
        PreparedStatement sumIncomeStmt = con.prepareStatement(sumIncomeQuery);
        sumIncomeStmt.setString(1, User_ID);
        ResultSet sumexpense = sumIncomeStmt.executeQuery();
        if (sumexpense.next()) {
            sumOfexpense = sumexpense.getDouble(1);
        }

        double currentBalance= 0.0;

        if (isIncome) {
            currentBalance = incomeAmount - sumOfexpense;
        } else {
            currentBalance = incomeAmount - sumOfexpense;
        }

        String updateBalanceQuery = "UPDATE balance_table SET BalanceAmount = ? WHERE UserID = ?";
        PreparedStatement updateBalanceStmt = con.prepareStatement(updateBalanceQuery);
        updateBalanceStmt.setDouble(1, currentBalance);
        updateBalanceStmt.setString(2, User_ID);
        updateBalanceStmt.executeUpdate();
    }

    void deleteTableRow() {
        int selectedRowIndex = jtable.getSelectedRow();
        if (selectedRowIndex != -1) {
            String categoryType = (String) model.getValueAt(selectedRowIndex, 1);
            String selectedExpenseID = (String) model.getValueAt(selectedRowIndex, 5);
            String selectedIncomeID = (String) model.getValueAt(selectedRowIndex, 6);
            double amountValue = (double) model.getValueAt(selectedRowIndex, 2);

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                con = DriverManager.getConnection("jdbc:mysql://localhost/expensetracker_db", "root", "");

                String tableName = "";
                String idColumnName = "";

                // Define table and column names based on category type
                if ("Expense".equals(categoryType)) {
                    tableName = "expense_table";
                    idColumnName = "ExpenseID";
                } else if ("Income".equals(categoryType)) {
                    tableName = "income_table";
                    idColumnName = "IncomeID";
                } else {
                    System.err.println("Unknown category type: " + categoryType);
                    return;
                }

                String deleteTransactionQuery = "DELETE FROM transaction_table WHERE " + idColumnName + " = ?";
                PreparedStatement deleteTransactionStmt = con.prepareStatement(deleteTransactionQuery);
                if ("Expense".equals(categoryType)) {
                    deleteTransactionStmt.setString(1, selectedExpenseID);
                } else if ("Income".equals(categoryType)) {
                    deleteTransactionStmt.setString(1, selectedIncomeID);
                }
                deleteTransactionStmt.executeUpdate();

                // Delete row from corresponding table
                String deleteQuery = "DELETE FROM " + tableName + " WHERE " + idColumnName + " = ?";
                PreparedStatement deleteStmt = con.prepareStatement(deleteQuery);
                if ("Expense".equals(categoryType)) {
                    deleteStmt.setString(1, selectedExpenseID);
                } else if ("Income".equals(categoryType)) {
                    deleteStmt.setString(1, selectedIncomeID);
                }
                int rowsAffected = deleteStmt.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Deleted!");
                    if ("Expense".equals(categoryType)) {
                    updateBalance(con, amountValue, false);
                } else if ("Income".equals(categoryType)) {
                    updateBalance(con, amountValue, true);
                }
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to delete row");
                }

                showtable();
                con.close();

            } catch (ClassNotFoundException | SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to delete row: " + ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select a row to delete");
        }
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        category = new javax.swing.JComboBox<>();
        jdate = new com.toedter.calendar.JDateChooser();
        button1 = new swing.Button();
        button2 = new swing.Button();
        updatebtn = new swing.Button();
        amount = new javax.swing.JTextField();
        IncomeID = new javax.swing.JTextField();
        expenseID = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        desc = new javax.swing.JTextPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtable = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 224, 224));
        jPanel1.setForeground(new java.awt.Color(173, 128, 171));

        jLabel1.setFont(new java.awt.Font("Lucida Fax", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(173, 128, 171));
        jLabel1.setText("Description");

        jLabel3.setFont(new java.awt.Font("Lucida Fax", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(173, 128, 171));
        jLabel3.setText("Category Type");

        jLabel4.setFont(new java.awt.Font("Lucida Fax", 1, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(173, 128, 171));
        jLabel4.setText("Amount");

        jLabel5.setFont(new java.awt.Font("Lucida Fax", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(173, 128, 171));
        jLabel5.setText("Date");

        category.setFont(new java.awt.Font("Lucida Fax", 0, 14)); // NOI18N
        category.setForeground(new java.awt.Color(209, 177, 208));
        category.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(244, 168, 185), 2, true));

        jdate.setBackground(new java.awt.Color(255, 224, 224));
        jdate.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(244, 168, 185), 2, true));
        jdate.setForeground(new java.awt.Color(255, 224, 224));

        button1.setBackground(new java.awt.Color(244, 198, 214));
        button1.setBorder(null);
        button1.setText("Delete");
        button1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button1ActionPerformed(evt);
            }
        });

        button2.setBackground(new java.awt.Color(244, 198, 214));
        button2.setBorder(null);
        button2.setText("Homepage");
        button2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button2ActionPerformed(evt);
            }
        });

        updatebtn.setBackground(new java.awt.Color(244, 198, 214));
        updatebtn.setBorder(null);
        updatebtn.setText("Update");
        updatebtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updatebtnActionPerformed(evt);
            }
        });

        amount.setFont(new java.awt.Font("Lucida Fax", 0, 14)); // NOI18N
        amount.setForeground(new java.awt.Color(173, 85, 146));
        amount.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(244, 168, 185), 3, true));
        amount.setCaretColor(new java.awt.Color(255, 0, 204));

        IncomeID.setEditable(false);
        IncomeID.setBackground(new java.awt.Color(255, 224, 224));
        IncomeID.setFont(new java.awt.Font("Lucida Fax", 0, 14)); // NOI18N
        IncomeID.setForeground(new java.awt.Color(255, 224, 224));
        IncomeID.setBorder(null);
        IncomeID.setCaretColor(new java.awt.Color(255, 0, 204));
        IncomeID.setEnabled(false);
        IncomeID.setOpaque(true);
        IncomeID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                IncomeIDActionPerformed(evt);
            }
        });

        expenseID.setEditable(false);
        expenseID.setBackground(new java.awt.Color(255, 224, 224));
        expenseID.setFont(new java.awt.Font("Lucida Fax", 0, 14)); // NOI18N
        expenseID.setForeground(new java.awt.Color(255, 224, 224));
        expenseID.setBorder(null);
        expenseID.setCaretColor(new java.awt.Color(255, 0, 204));
        expenseID.setEnabled(false);
        expenseID.setOpaque(true);

        desc.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(244, 168, 185), 2, true));
        jScrollPane2.setViewportView(desc);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(80, 80, 80)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(button1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(button2, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                            .addComponent(updatebtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(58, 58, 58)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jdate, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(category, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(amount, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(60, 60, 60))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap(40, Short.MAX_VALUE)
                        .addComponent(expenseID, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(240, 240, 240)
                        .addComponent(IncomeID, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(59, 59, 59))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(category, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(amount, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jdate, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                .addComponent(IncomeID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(expenseID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45)
                .addComponent(updatebtn, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(button1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(button2, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel8.setFont(new java.awt.Font("Lucida Fax", 1, 36)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(74, 74, 74));
        jLabel8.setText("HISTORY RECORDS");
        jLabel8.setToolTipText("");

        jtable.setFont(new java.awt.Font("Lucida Fax", 1, 14)); // NOI18N
        jtable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Category Name", "Category Type", "Amount", "Description", "Date", "Expense ID", "Income ID"
            }
        ));
        jScrollPane1.setViewportView(jtable);

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-payment-history-40 (1).png"))); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 782, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 559, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void button1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button1ActionPerformed
        // TODO add your handling code here:
        deleteTableRow();
    }//GEN-LAST:event_button1ActionPerformed

    private void button2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button2ActionPerformed
        // TODO add your handling code here:

        new ET_Homepage().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_button2ActionPerformed

    private void updatebtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updatebtnActionPerformed

        updateTableRow();


    }//GEN-LAST:event_updatebtnActionPerformed

    private void IncomeIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_IncomeIDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_IncomeIDActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Historypage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Historypage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Historypage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Historypage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Historypage().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField IncomeID;
    private javax.swing.JTextField amount;
    private swing.Button button1;
    private swing.Button button2;
    private javax.swing.JComboBox<String> category;
    private javax.swing.JTextPane desc;
    private javax.swing.JTextField expenseID;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private com.toedter.calendar.JDateChooser jdate;
    private javax.swing.JTable jtable;
    private swing.Button updatebtn;
    // End of variables declaration//GEN-END:variables
}
