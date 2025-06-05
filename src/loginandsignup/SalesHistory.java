package loginandsignup;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.TitledBorder;
import javax.swing.ListSelectionModel;

public class SalesHistory extends JFrame {
    private JTable salesTable;
    private DefaultTableModel salesTableModel;
    private final DecimalFormat df = new DecimalFormat("#,##0.00");
    private JPanel summaryPanel;
    private JSpinner startDateSpinner;
    private JSpinner endDateSpinner;
    private JComboBox<String> monthCombo;
    private JComboBox<Integer> yearCombo;
    private JLabel totalLabel;
    private Color PRIMARY_COLOR = new Color(26, 155, 118); // Match Home class color

    public SalesHistory() {
        setTitle("Sales History");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        
        // Create main content panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Color.WHITE);

        // Add filter panel at the top
        mainPanel.add(createFilterPanel(), BorderLayout.NORTH);

        // Create table
        createSalesTable();
        JScrollPane scrollPane = new JScrollPane(salesTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Create summary panel
        summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR),
                "Sales Summary",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                PRIMARY_COLOR
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JScrollPane summaryScrollPane = new JScrollPane(summaryPanel);
        summaryScrollPane.setPreferredSize(new Dimension(300, 0));
        mainPanel.add(summaryScrollPane, BorderLayout.EAST);

        add(mainPanel);

        // Load initial sales data
        loadSalesData(null, null);
    }

    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel(new BorderLayout(10, 10));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Create month/year filter
        JPanel monthYearPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        monthYearPanel.setBackground(Color.WHITE);

        String[] months = {"January", "February", "March", "April", "May", "June", 
                          "July", "August", "September", "October", "November", "December"};
        monthCombo = new JComboBox<>(months);
        monthCombo.setPreferredSize(new Dimension(100, 30));

        Integer[] years = new Integer[10];
        int currentYear = LocalDate.now().getYear();
        for (int i = 0; i < 10; i++) {
            years[i] = currentYear - i;
        }
        yearCombo = new JComboBox<>(years);
        yearCombo.setPreferredSize(new Dimension(80, 30));

        JButton monthFilterBtn = new JButton("View Month");
        styleButton(monthFilterBtn);
        monthFilterBtn.addActionListener(e -> filterByMonth());

        monthYearPanel.add(new JLabel("Month:"));
        monthYearPanel.add(monthCombo);
        monthYearPanel.add(new JLabel("Year:"));
        monthYearPanel.add(yearCombo);
        monthYearPanel.add(monthFilterBtn);

        // Create date range filter
        JPanel dateRangePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        dateRangePanel.setBackground(Color.WHITE);

        // Create date spinners
        Calendar calendar = Calendar.getInstance();
        Date initDate = calendar.getTime();
        calendar.add(Calendar.YEAR, -10);
        Date earliestDate = calendar.getTime();
        calendar.add(Calendar.YEAR, 20);
        Date latestDate = calendar.getTime();

        SpinnerDateModel startModel = new SpinnerDateModel(initDate, earliestDate, latestDate, Calendar.DAY_OF_MONTH);
        SpinnerDateModel endModel = new SpinnerDateModel(initDate, earliestDate, latestDate, Calendar.DAY_OF_MONTH);
        
        startDateSpinner = new JSpinner(startModel);
        endDateSpinner = new JSpinner(endModel);
        
        JSpinner.DateEditor startEditor = new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd");
        JSpinner.DateEditor endEditor = new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd");
        startDateSpinner.setEditor(startEditor);
        endDateSpinner.setEditor(endEditor);
        
        startDateSpinner.setPreferredSize(new Dimension(120, 30));
        endDateSpinner.setPreferredSize(new Dimension(120, 30));

        JButton dateFilterBtn = new JButton("View Range");
        styleButton(dateFilterBtn);
        dateFilterBtn.addActionListener(e -> filterByDateRange());

        dateRangePanel.add(new JLabel("From:"));
        dateRangePanel.add(startDateSpinner);
        dateRangePanel.add(new JLabel("To:"));
        dateRangePanel.add(endDateSpinner);
        dateRangePanel.add(dateFilterBtn);

        // Add total amount label
        totalLabel = new JLabel("Total: ₱0.00");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalLabel.setForeground(PRIMARY_COLOR);
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.setBackground(Color.WHITE);
        totalPanel.add(totalLabel);

        // Combine all panels
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.add(monthYearPanel, BorderLayout.WEST);
        topPanel.add(dateRangePanel, BorderLayout.CENTER);
        topPanel.add(totalPanel, BorderLayout.EAST);

        filterPanel.add(topPanel);
        return filterPanel;
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY_COLOR);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setFocusPainted(false);
    }

    private void createSalesTable() {
        salesTableModel = new DefaultTableModel(
            new Object[]{"Date", "Time", "Items", "Total Amount", "Payment Method"}, 
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        salesTable = new JTable(salesTableModel);
        salesTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        salesTable.setRowHeight(30);
        salesTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        salesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        salesTable.setShowGrid(true);
        salesTable.setGridColor(new Color(230, 230, 230));
    }

    private void filterByMonth() {
        int month = monthCombo.getSelectedIndex() + 1;
        int year = (Integer) yearCombo.getSelectedItem();
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        loadSalesData(startDate, endDate);
    }

    private void filterByDateRange() {
        Date startDate = (Date) startDateSpinner.getValue();
        Date endDate = (Date) endDateSpinner.getValue();
        
        if (startDate == null || endDate == null) {
            JOptionPane.showMessageDialog(this, "Please select both start and end dates");
            return;
        }
        
        LocalDate start = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate end = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        loadSalesData(start, end);
    }

    private void loadSalesData(LocalDate startDate, LocalDate endDate) {
        try {
            Connection conn = getConnection();
            
            StringBuilder sql = new StringBuilder(
                "SELECT s.sale_id, s.sale_date, s.total_amount, s.payment_method, " +
                "GROUP_CONCAT(CONCAT(si.quantity, 'x ', p.product_name) SEPARATOR ', ') as items " +
                "FROM sales s " +
                "LEFT JOIN sale_items si ON s.sale_id = si.sale_id " +
                "LEFT JOIN products p ON si.product_id = p.product_id "
            );

            if (startDate != null && endDate != null) {
                sql.append("WHERE DATE(s.sale_date) BETWEEN ? AND ? ");
            }

            sql.append("GROUP BY s.sale_id ORDER BY s.sale_date DESC");
            
            PreparedStatement pstmt = conn.prepareStatement(sql.toString());
            if (startDate != null && endDate != null) {
                pstmt.setString(1, startDate.toString());
                pstmt.setString(2, endDate.toString());
            }

            ResultSet rs = pstmt.executeQuery();

            // Clear existing data
            salesTableModel.setRowCount(0);
            summaryPanel.removeAll();

            // Maps for different summaries
            HashMap<LocalDate, Double> dailyTotals = new HashMap<>();
            double grandTotal = 0.0;

            while (rs.next()) {
                Timestamp saleDate = rs.getTimestamp("sale_date");
                LocalDate date = saleDate.toLocalDateTime().toLocalDate();
                String time = saleDate.toLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                double amount = rs.getDouble("total_amount");
                String items = rs.getString("items");
                String paymentMethod = rs.getString("payment_method");

                // Add to table
                salesTableModel.addRow(new Object[]{
                    date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    time,
                    items,
                    "₱" + df.format(amount),
                    paymentMethod
                });

                // Update totals
                dailyTotals.merge(date, amount, Double::sum);
                grandTotal += amount;
            }

            // Update total label
            totalLabel.setText("Total: ₱" + df.format(grandTotal));

            // Display daily totals
            JLabel summaryTitle = new JLabel("Daily Sales");
            summaryTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
            summaryTitle.setAlignmentX(JLabel.LEFT_ALIGNMENT);
            summaryPanel.add(summaryTitle);
            summaryPanel.add(Box.createVerticalStrut(10));

            dailyTotals.entrySet().stream()
                .sorted((e1, e2) -> e2.getKey().compareTo(e1.getKey()))
                .forEach(entry -> {
                    JPanel dayPanel = new JPanel(new BorderLayout(10, 0));
                    dayPanel.setBackground(Color.WHITE);
                    dayPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
                    dayPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

                    JLabel dateLabel = new JLabel(entry.getKey().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
                    dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    
                    JLabel amountLabel = new JLabel("₱" + df.format(entry.getValue()));
                    amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    amountLabel.setForeground(PRIMARY_COLOR);

                    dayPanel.add(dateLabel, BorderLayout.WEST);
                    dayPanel.add(amountLabel, BorderLayout.EAST);
                    summaryPanel.add(dayPanel);
                });

            rs.close();
            pstmt.close();
            conn.close();

            // Refresh UI
            summaryPanel.revalidate();
            summaryPanel.repaint();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading sales data: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private Connection getConnection() throws Exception {
        String url = "jdbc:mysql://localhost:3306/java_user_database";
        String username = "root";
        String password = "";
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(url, username, password);
    }
}