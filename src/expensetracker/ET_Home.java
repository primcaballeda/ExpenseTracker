/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package expensetracker;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.sql.Date;
import java.time.YearMonth;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 *
 * @author Prim
 */
public class ET_Home extends javax.swing.JFrame {

    Connection con;
    PreparedStatement stm;

    Login_page login = new Login_page();
    static String next = "";

    public ET_Home() {

        initComponents();
        ExTableCustom();
        Connect();
        login.info(next);
        User_ID = login.userId;

        showAll();

        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
    public String User_ID;

    public void Connect() {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/expensetracker_db", "root", "");

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ET_Home.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ET_Home.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    void showAll() {

        showtable();
        showTotalSpentThisMonth();
        showTotalTransportation();
        showTotalUtilities();
        showTotalFood();
        showTotalOthers();
        displayIncome();
        displayName();
        displayBalance();
        displaySpent();

    }

    public void showTotalSpentThisMonth() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/expensetracker_db", "root", "");

            String query = "SELECT SUM(e.Amount) AS TotalSpentThisMonth "
                    + "FROM transaction_table t "
                    + "LEFT JOIN expense_table e ON t.ExpenseID = e.ExpenseID "
                    + "WHERE t.UserID = ? AND MONTH(e.Date) = ? AND YEAR(e.Date) = ?";

            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, User_ID);
            YearMonth currentMonth = YearMonth.now();
            int month = currentMonth.getMonthValue();
            int year = currentMonth.getYear();

            stmt.setInt(2, month);
            stmt.setInt(3, year);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String totalSpentThisMonth = rs.getString("TotalSpentThisMonth");
                 if(totalSpentThisMonth!=null){
                mspent.setText(totalSpentThisMonth);
            }else{
                     mspent.setText("0");
                 }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showTotalTransportation() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/expensetracker_db", "root", "");

            String query = "SELECT SUM(e.Amount) AS TotalTransportation "
                    + "FROM transaction_table t "
                    + "LEFT JOIN expense_table e ON t.ExpenseID = e.ExpenseID "
                    + "LEFT JOIN category_table c ON c.CategoryID = e.CategoryID "
                    + "WHERE t.UserID = ? AND e.Date = ? AND c.CategoryName = 'Transportation'";

            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, User_ID);

            LocalDate today = LocalDate.now();
            stmt.setDate(2, Date.valueOf(today));

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String totalTransportation = rs.getString("TotalTransportation");
                if(totalTransportation!=null){
                trans.setText(totalTransportation);
            }else{
                    trans.setText("0");
                }

}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showTotalUtilities() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/expensetracker_db", "root", "");

            String query = "SELECT SUM(e.Amount) AS TotalUtility "
                    + "FROM transaction_table t "
                    + "LEFT JOIN expense_table e ON t.ExpenseID = e.ExpenseID "
                    + "LEFT JOIN category_table c ON c.CategoryID = e.CategoryID "
                    + "WHERE t.UserID = ? AND e.Date = ? AND c.CategoryName = 'Utilities'";

            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, User_ID);

            LocalDate today = LocalDate.now();
            stmt.setDate(2, Date.valueOf(today));

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String totalUtility = rs.getString("TotalUtility");
                if (totalUtility != null) {

                    utils.setText(totalUtility);
                } else {
                    utils.setText("0");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showTotalFood() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/expensetracker_db", "root", "");

            String query = "SELECT SUM(e.Amount) AS TotalFood "
                    + "FROM transaction_table t "
                    + "LEFT JOIN expense_table e ON t.ExpenseID = e.ExpenseID "
                    + "LEFT JOIN category_table c ON c.CategoryID = e.CategoryID "
                    + "WHERE t.UserID = ? AND e.Date = ? AND c.CategoryName = 'Food'";

            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, User_ID);

            LocalDate today = LocalDate.now();
            stmt.setDate(2, Date.valueOf(today));

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String totalFood = rs.getString("TotalFood");
                if (totalFood != null) {
                    foods.setText(totalFood);
                } else {
                    foods.setText("0");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showTotalOthers() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/expensetracker_db", "root", "");

            String query = "SELECT SUM(e.Amount) AS TotalOthers "
                    + "FROM transaction_table t "
                    + "LEFT JOIN expense_table e ON t.ExpenseID = e.ExpenseID "
                    + "LEFT JOIN category_table c ON c.CategoryID = e.CategoryID "
                    + "WHERE t.UserID = ? AND e.Date = ? AND c.CategoryName = 'Others'";

            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, User_ID);

            LocalDate today = LocalDate.now();
            stmt.setDate(2, Date.valueOf(today));

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String totalOthers = rs.getString("TotalOthers");
                if (totalOthers != null) {
                    others.setText(totalOthers);
                } else {
                    others.setText("0");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void displayIncome() {
        try {
            PreparedStatement ps = con.prepareStatement("SELECT SUM(Amount) AS Amount FROM income_table WHERE UserID = ?");
            ps.setString(1, User_ID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String totalSpent = rs.getString("Amount");
                if (totalSpent != null) {
                    income1.setText(totalSpent);
                    income1.revalidate();
                    income1.repaint();

                } else {
                    income1.setText("0");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void displaySpent() {
        try {

            PreparedStatement ps = con.prepareStatement("SELECT SUM(Amount) AS Amount FROM expense_table WHERE UserID = ?");
            ps.setString(1, User_ID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String totalSpent = rs.getString("Amount");
                if (totalSpent != null) {
                    spent.setText(totalSpent);
                } else {
                    spent.setText("0");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    void displayBalance() {
        try {
            PreparedStatement ps = con.prepareStatement("SELECT BalanceAmount FROM balance_table WHERE UserID= '" + User_ID + "'");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String balancee = rs.getString("BalanceAmount");
                balance.setText(balancee);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void displayName() {

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/expensetracker_db", "root", "");
            PreparedStatement ps = con.prepareStatement("SELECT Fullname FROM users_table WHERE UserID= '" + User_ID + "'");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String fulln = rs.getString("fullname");
                String firstName = fulln.split("\\s+")[0];

                names.setText("Hello, " + firstName + "!");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static DefaultTableModel model;

    public void showtable() {
        model = (DefaultTableModel) jtable.getModel();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/expensetracker_db", "root", "");

            String query = "SELECT t.ExpenseID, c.CategoryName, c.CategoryType, "
                    + "e.Amount, e.Description, e.Date "
                    + "FROM transaction_table t "
                    + "LEFT JOIN expense_table e ON t.ExpenseID = e.ExpenseID "
                    + "LEFT JOIN category_table c ON c.CategoryID = e.CategoryID "
                    + "WHERE t.UserID = ? AND e.Date = ?";

            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, User_ID);

            LocalDate today = LocalDate.now();
            stmt.setDate(2, Date.valueOf(today));

            ResultSet rs = stmt.executeQuery();

            model.setRowCount(0);

            while (rs.next()) {
                String categoryName = rs.getString("CategoryName");
                double amount = rs.getDouble("Amount");
                String description = rs.getString("Description");
                Date date = rs.getDate("Date");

                model.addRow(new Object[]{categoryName, amount, description, date});
            }

        } catch (Exception e) {
            e.printStackTrace();
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
        roundPanel2 = new swing.RoundPanel();
        jLabel1 = new javax.swing.JLabel();
        button2 = new swing.Button();
        button3 = new swing.Button();
        button4 = new swing.Button();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        names = new javax.swing.JLabel();
        roundPanel1 = new swing.RoundPanel();
        jLabel4 = new javax.swing.JLabel();
        balance = new javax.swing.JLabel();
        roundPanel3 = new swing.RoundPanel();
        jLabel5 = new javax.swing.JLabel();
        balance1 = new javax.swing.JLabel();
        roundPanel4 = new swing.RoundPanel();
        jLabel6 = new javax.swing.JLabel();
        balance2 = new javax.swing.JLabel();
        roundPanel5 = new swing.RoundPanel();
        jLabel7 = new javax.swing.JLabel();
        balance3 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        roundPanel6 = new swing.RoundPanel();
        jLabel8 = new javax.swing.JLabel();
        income1 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        roundPanel7 = new swing.RoundPanel();
        jLabel11 = new javax.swing.JLabel();
        spent = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtable = new javax.swing.JTable();
        jLabel13 = new javax.swing.JLabel();
        roundPanel8 = new swing.RoundPanel();
        jLabel14 = new javax.swing.JLabel();
        mspent = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        roundPanel9 = new swing.RoundPanel();
        jLabel16 = new javax.swing.JLabel();
        roundPanel10 = new swing.RoundPanel();
        jLabel17 = new javax.swing.JLabel();
        others = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        roundPanel11 = new swing.RoundPanel();
        jLabel20 = new javax.swing.JLabel();
        trans = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        roundPanel12 = new swing.RoundPanel();
        jLabel23 = new javax.swing.JLabel();
        utils = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        roundPanel13 = new swing.RoundPanel();
        jLabel26 = new javax.swing.JLabel();
        foods = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 245, 248));

        roundPanel2.setBackground(new java.awt.Color(255, 224, 224));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Screenshot 2024-05-16 155711 (2).png"))); // NOI18N
        roundPanel2.add(jLabel1);
        jLabel1.setBounds(30, 20, 120, 60);

        button2.setBackground(new java.awt.Color(244, 198, 214));
        button2.setForeground(new java.awt.Color(74, 74, 74));
        button2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-payment-history-40 (1).png"))); // NOI18N
        button2.setText("History Records");
        button2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button2ActionPerformed(evt);
            }
        });
        roundPanel2.add(button2);
        button2.setBounds(20, 410, 180, 60);

        button3.setBackground(new java.awt.Color(244, 198, 214));
        button3.setForeground(new java.awt.Color(74, 74, 74));
        button3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-give-money-40.png"))); // NOI18N
        button3.setText("Add Expense");
        button3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button3ActionPerformed(evt);
            }
        });
        roundPanel2.add(button3);
        button3.setBounds(20, 330, 180, 60);

        button4.setBackground(new java.awt.Color(244, 198, 214));
        button4.setForeground(new java.awt.Color(74, 74, 74));
        button4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-add-money-40.png"))); // NOI18N
        button4.setText("Add Income");
        button4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button4ActionPerformed(evt);
            }
        });
        roundPanel2.add(button4);
        button4.setBounds(20, 250, 180, 60);

        jPanel2.setBackground(new java.awt.Color(255, 224, 224));
        jPanel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel2MouseClicked(evt);
            }
        });

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-logout-40 (2).png"))); // NOI18N

        jLabel3.setFont(new java.awt.Font("Lucida Fax", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(74, 74, 74));
        jLabel3.setText("Logout");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        roundPanel2.add(jPanel2);
        jPanel2.setBounds(30, 580, 160, 60);

        names.setFont(new java.awt.Font("Lucida Fax", 1, 24)); // NOI18N
        names.setForeground(new java.awt.Color(74, 74, 74));
        names.setText("Hello, User!");
        roundPanel2.add(names);
        names.setBounds(20, 120, 180, 30);

        roundPanel1.setBackground(new java.awt.Color(255, 224, 224));

        jLabel4.setFont(new java.awt.Font("Lucida Fax", 1, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 74, 74));
        jLabel4.setText("Current Balance");
        roundPanel1.add(jLabel4);
        jLabel4.setBounds(30, 20, 240, 40);

        balance.setFont(new java.awt.Font("Lucida Fax", 1, 30)); // NOI18N
        balance.setForeground(new java.awt.Color(74, 74, 74));
        balance.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        balance.setText("0 ");
        roundPanel1.add(balance);
        balance.setBounds(60, 60, 180, 36);

        roundPanel3.setBackground(new java.awt.Color(255, 224, 224));

        jLabel5.setFont(new java.awt.Font("Lucida Fax", 1, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 74, 74));
        jLabel5.setText("Current Balance");
        roundPanel3.add(jLabel5);
        jLabel5.setBounds(30, 20, 240, 40);

        balance1.setFont(new java.awt.Font("Lucida Fax", 1, 30)); // NOI18N
        balance1.setForeground(new java.awt.Color(74, 74, 74));
        balance1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        balance1.setText("0 peso/s");
        roundPanel3.add(balance1);
        balance1.setBounds(100, 70, 180, 36);

        roundPanel1.add(roundPanel3);
        roundPanel3.setBounds(0, 0, 0, 0);

        roundPanel4.setBackground(new java.awt.Color(255, 224, 224));

        jLabel6.setFont(new java.awt.Font("Lucida Fax", 1, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 74, 74));
        jLabel6.setText("Current Balance");
        roundPanel4.add(jLabel6);
        jLabel6.setBounds(30, 20, 240, 40);

        balance2.setFont(new java.awt.Font("Lucida Fax", 1, 30)); // NOI18N
        balance2.setForeground(new java.awt.Color(74, 74, 74));
        balance2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        balance2.setText("0 peso/s");
        roundPanel4.add(balance2);
        balance2.setBounds(100, 70, 180, 36);

        roundPanel5.setBackground(new java.awt.Color(255, 224, 224));

        jLabel7.setFont(new java.awt.Font("Lucida Fax", 1, 24)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 74, 74));
        jLabel7.setText("Current Balance");
        roundPanel5.add(jLabel7);
        jLabel7.setBounds(30, 20, 240, 40);

        balance3.setFont(new java.awt.Font("Lucida Fax", 1, 30)); // NOI18N
        balance3.setForeground(new java.awt.Color(74, 74, 74));
        balance3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        balance3.setText("0 peso/s");
        roundPanel5.add(balance3);
        balance3.setBounds(100, 70, 180, 36);

        roundPanel4.add(roundPanel5);
        roundPanel5.setBounds(0, 0, 0, 0);

        roundPanel1.add(roundPanel4);
        roundPanel4.setBounds(0, 0, 0, 0);

        jLabel12.setFont(new java.awt.Font("Segoe UI", 0, 30)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(74, 74, 74));
        jLabel12.setText("₱");
        roundPanel1.add(jLabel12);
        jLabel12.setBounds(30, 60, 30, 40);

        roundPanel6.setBackground(new java.awt.Color(255, 224, 224));

        jLabel8.setFont(new java.awt.Font("Lucida Fax", 1, 24)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 74, 74));
        jLabel8.setText("Total Income");
        roundPanel6.add(jLabel8);
        jLabel8.setBounds(30, 20, 240, 40);

        income1.setFont(new java.awt.Font("Lucida Fax", 1, 30)); // NOI18N
        income1.setForeground(new java.awt.Color(74, 74, 74));
        income1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        income1.setText("0 ");
        roundPanel6.add(income1);
        income1.setBounds(50, 60, 200, 36);

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 30)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(74, 74, 74));
        jLabel9.setText("₱");
        roundPanel6.add(jLabel9);
        jLabel9.setBounds(30, 60, 30, 30);

        roundPanel7.setBackground(new java.awt.Color(255, 224, 224));

        jLabel11.setFont(new java.awt.Font("Lucida Fax", 1, 24)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(74, 74, 74));
        jLabel11.setText("Total Spent");
        roundPanel7.add(jLabel11);
        jLabel11.setBounds(20, 20, 219, 30);

        spent.setFont(new java.awt.Font("Lucida Fax", 1, 30)); // NOI18N
        spent.setForeground(new java.awt.Color(74, 74, 74));
        spent.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        spent.setText("0 ");
        roundPanel7.add(spent);
        spent.setBounds(80, 60, 150, 36);

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 30)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(74, 74, 74));
        jLabel10.setText("₱");
        roundPanel7.add(jLabel10);
        jLabel10.setBounds(40, 60, 30, 40);

        jtable.setFont(new java.awt.Font("Lucida Fax", 1, 14)); // NOI18N
        jtable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Category Name", "Amount", "Description", "Date"
            }
        ));
        jScrollPane1.setViewportView(jtable);

        jLabel13.setFont(new java.awt.Font("Lucida Fax", 1, 24)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(0, 74, 74));
        jLabel13.setText("Expense Today");

        roundPanel8.setBackground(new java.awt.Color(255, 224, 224));

        jLabel14.setFont(new java.awt.Font("Lucida Fax", 1, 24)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(74, 74, 74));
        jLabel14.setText("Monthly Spent");
        roundPanel8.add(jLabel14);
        jLabel14.setBounds(30, 20, 219, 30);

        mspent.setFont(new java.awt.Font("Lucida Fax", 1, 30)); // NOI18N
        mspent.setForeground(new java.awt.Color(74, 74, 74));
        mspent.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        mspent.setText("0 ");
        roundPanel8.add(mspent);
        mspent.setBounds(60, 60, 150, 36);

        jLabel15.setFont(new java.awt.Font("Segoe UI", 0, 30)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(74, 74, 74));
        jLabel15.setText("₱");
        roundPanel8.add(jLabel15);
        jLabel15.setBounds(30, 60, 30, 40);

        roundPanel9.setBackground(new java.awt.Color(255, 224, 224));

        jLabel16.setFont(new java.awt.Font("Lucida Fax", 1, 24)); // NOI18N
        jLabel16.setText("Transactions");
        roundPanel9.add(jLabel16);
        jLabel16.setBounds(80, 10, 180, 30);

        roundPanel10.setBackground(new java.awt.Color(255, 255, 255));

        jLabel17.setFont(new java.awt.Font("Lucida Fax", 1, 18)); // NOI18N
        jLabel17.setText("Others");
        roundPanel10.add(jLabel17);
        jLabel17.setBounds(20, 30, 150, 20);

        others.setFont(new java.awt.Font("Lucida Fax", 1, 18)); // NOI18N
        others.setText("0");
        roundPanel10.add(others);
        others.setBounds(210, 30, 80, 20);

        jLabel19.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(74, 74, 74));
        jLabel19.setText("₱");
        roundPanel10.add(jLabel19);
        jLabel19.setBounds(190, 30, 20, 20);

        roundPanel9.add(roundPanel10);
        roundPanel10.setBounds(20, 360, 300, 80);

        roundPanel11.setBackground(new java.awt.Color(255, 255, 255));

        jLabel20.setFont(new java.awt.Font("Lucida Fax", 1, 18)); // NOI18N
        jLabel20.setText("Transportation");
        roundPanel11.add(jLabel20);
        jLabel20.setBounds(20, 30, 150, 20);

        trans.setFont(new java.awt.Font("Lucida Fax", 1, 18)); // NOI18N
        trans.setText("0");
        roundPanel11.add(trans);
        trans.setBounds(210, 30, 80, 20);

        jLabel22.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(74, 74, 74));
        jLabel22.setText("₱");
        roundPanel11.add(jLabel22);
        jLabel22.setBounds(190, 30, 20, 20);

        roundPanel9.add(roundPanel11);
        roundPanel11.setBounds(20, 60, 300, 80);

        roundPanel12.setBackground(new java.awt.Color(255, 255, 255));

        jLabel23.setFont(new java.awt.Font("Lucida Fax", 1, 18)); // NOI18N
        jLabel23.setText("Utilities");
        roundPanel12.add(jLabel23);
        jLabel23.setBounds(20, 30, 150, 20);

        utils.setFont(new java.awt.Font("Lucida Fax", 1, 18)); // NOI18N
        utils.setText("0");
        roundPanel12.add(utils);
        utils.setBounds(210, 30, 80, 20);

        jLabel25.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(74, 74, 74));
        jLabel25.setText("₱");
        roundPanel12.add(jLabel25);
        jLabel25.setBounds(190, 30, 20, 20);

        roundPanel9.add(roundPanel12);
        roundPanel12.setBounds(20, 160, 300, 80);

        roundPanel13.setBackground(new java.awt.Color(255, 255, 255));

        jLabel26.setFont(new java.awt.Font("Lucida Fax", 1, 18)); // NOI18N
        jLabel26.setText("Food");
        roundPanel13.add(jLabel26);
        jLabel26.setBounds(20, 30, 150, 20);

        foods.setFont(new java.awt.Font("Lucida Fax", 1, 18)); // NOI18N
        foods.setText("0");
        roundPanel13.add(foods);
        foods.setBounds(210, 30, 80, 20);

        jLabel28.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(74, 74, 74));
        jLabel28.setText("₱");
        roundPanel13.add(jLabel28);
        jLabel28.setBounds(190, 30, 20, 20);

        roundPanel9.add(roundPanel13);
        roundPanel13.setBounds(20, 260, 300, 80);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(roundPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(roundPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)
                        .addComponent(roundPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(roundPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(roundPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 748, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)
                        .addComponent(roundPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, 344, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(36, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(roundPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(roundPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
                            .addComponent(roundPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(roundPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(roundPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(45, 45, 45)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(roundPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 463, Short.MAX_VALUE))
                        .addGap(0, 67, Short.MAX_VALUE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void button3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button3ActionPerformed
        // TODO add your handling code here:
        new expense().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_button3ActionPerformed

    private void button2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button2ActionPerformed
        // TODO add your handling code here:
        new Historypage().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_button2ActionPerformed

    private void jPanel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel2MouseClicked
        // TODO add your handling code here:
        new Login_page().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jPanel2MouseClicked

    private void button4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button4ActionPerformed
        // TODO add your handling code here:
        new Income().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_button4ActionPerformed

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
            java.util.logging.Logger.getLogger(ET_Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ET_Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ET_Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ET_Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ET_Home().setVisible(true);
                ET_Home home = new ET_Home();
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel balance;
    private javax.swing.JLabel balance1;
    private javax.swing.JLabel balance2;
    private javax.swing.JLabel balance3;
    private swing.Button button2;
    private swing.Button button3;
    private swing.Button button4;
    private javax.swing.JLabel foods;
    private javax.swing.JLabel income1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jtable;
    private javax.swing.JLabel mspent;
    private javax.swing.JLabel names;
    private javax.swing.JLabel others;
    private swing.RoundPanel roundPanel1;
    private swing.RoundPanel roundPanel10;
    private swing.RoundPanel roundPanel11;
    private swing.RoundPanel roundPanel12;
    private swing.RoundPanel roundPanel13;
    private swing.RoundPanel roundPanel2;
    private swing.RoundPanel roundPanel3;
    private swing.RoundPanel roundPanel4;
    private swing.RoundPanel roundPanel5;
    private swing.RoundPanel roundPanel6;
    private swing.RoundPanel roundPanel7;
    private swing.RoundPanel roundPanel8;
    private swing.RoundPanel roundPanel9;
    private javax.swing.JLabel spent;
    private javax.swing.JLabel trans;
    private javax.swing.JLabel utils;
    // End of variables declaration//GEN-END:variables
}
