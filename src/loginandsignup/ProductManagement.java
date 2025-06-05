package loginandsignup;

import java.awt.*;
import java.sql.*;
import java.util.Random;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ProductManagement extends JFrame {
    private DefaultTableModel productTableModel;
    private JTable productTable;
    private JTextField nameField;
    private JTextField barcodeField;
    private JTextField priceField;
    private JTextField stockField;
    private JTextArea descriptionArea;
    private JComboBox<String> categoryCombo;
    private final Home parentFrame;
    private final Random random = new Random();

    public ProductManagement(Home parent) {
        this.parentFrame = parent;
        initComponents();
        loadProducts();
    }

    private void initComponents() {
        setTitle("Product Management");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Product list
        productTableModel = new DefaultTableModel(
            new Object[]{"ID", "Barcode", "Name", "Price", "Stock", "Category"}, 0
        );
        productTable = new JTable(productTableModel);
        JScrollPane scrollPane = new JScrollPane(productTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Barcode
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Barcode:"), gbc);
        
        // Create barcode panel with text field and generate button
        JPanel barcodePanel = new JPanel(new BorderLayout(5, 0));
        barcodeField = new JTextField(15);
        JButton generateBtn = new JButton("Generate");
        generateBtn.addActionListener(e -> generateBarcode());
        barcodePanel.add(barcodeField, BorderLayout.CENTER);
        barcodePanel.add(generateBtn, BorderLayout.EAST);
        
        gbc.gridx = 1;
        formPanel.add(barcodePanel, gbc);

        // Name
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);

        // Price
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Price:"), gbc);
        gbc.gridx = 1;
        priceField = new JTextField(20);
        formPanel.add(priceField, gbc);

        // Stock
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Stock:"), gbc);
        gbc.gridx = 1;
        stockField = new JTextField(20);
        formPanel.add(stockField, gbc);

        // Category
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1;
        categoryCombo = new JComboBox<>();
        loadCategories();
        formPanel.add(categoryCombo, gbc);

        // Description
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        formPanel.add(new JScrollPane(descriptionArea), gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addButton = new JButton("Add Product");
        JButton updateButton = new JButton("Update Selected");
        JButton deleteButton = new JButton("Delete Selected");
        JButton clearButton = new JButton("Clear Form");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        mainPanel.add(formPanel, BorderLayout.SOUTH);
        add(mainPanel);

        // Add action listeners
        addButton.addActionListener(e -> addProduct());
        updateButton.addActionListener(e -> updateProduct());
        deleteButton.addActionListener(e -> deleteProduct());
        clearButton.addActionListener(e -> clearForm());

        productTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedProduct();
            }
        });
    }

    private void loadCategories() {
        categoryCombo.removeAllItems();
        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT category_name FROM categories");
            
            while (rs.next()) {
                categoryCombo.addItem(rs.getString("category_name"));
            }
            
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading categories: " + e.getMessage());
        }
    }

    private void loadProducts() {
        productTableModel.setRowCount(0);
        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                "SELECT product_id, barcode, product_name, price, stock_quantity, category FROM products"
            );
            
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("product_id"));
                row.add(rs.getString("barcode"));
                row.add(rs.getString("product_name"));
                row.add(rs.getDouble("price"));
                row.add(rs.getInt("stock_quantity"));
                row.add(rs.getString("category"));
                productTableModel.addRow(row);
            }
            
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading products: " + e.getMessage());
        }
    }

    private void loadSelectedProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow >= 0) {
            barcodeField.setText(productTableModel.getValueAt(selectedRow, 1).toString());
            nameField.setText(productTableModel.getValueAt(selectedRow, 2).toString());
            priceField.setText(productTableModel.getValueAt(selectedRow, 3).toString());
            stockField.setText(productTableModel.getValueAt(selectedRow, 4).toString());
            String category = (String) productTableModel.getValueAt(selectedRow, 5);
            categoryCombo.setSelectedItem(category);
            
            // Load description from database
            try {
                Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(
                    "SELECT description FROM products WHERE product_id = ?"
                );
                pstmt.setInt(1, (Integer) productTableModel.getValueAt(selectedRow, 0));
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    descriptionArea.setText(rs.getString("description"));
                }
                
                rs.close();
                pstmt.close();
                conn.close();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error loading product description: " + e.getMessage());
            }
        }
    }

    private void addProduct() {
        try {
            validateInput();
            
            Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO products (barcode, product_name, price, stock_quantity, category, description) " +
                "VALUES (?, ?, ?, ?, ?, ?)"
            );
            
            pstmt.setString(1, barcodeField.getText());
            pstmt.setString(2, nameField.getText());
            pstmt.setDouble(3, Double.parseDouble(priceField.getText()));
            pstmt.setInt(4, Integer.parseInt(stockField.getText()));
            pstmt.setString(5, (String) categoryCombo.getSelectedItem());
            pstmt.setString(6, descriptionArea.getText());
            
            pstmt.executeUpdate();
            pstmt.close();
            conn.close();
            
            loadProducts();
            clearForm();
            JOptionPane.showMessageDialog(this, "Product added successfully!");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding product: " + e.getMessage());
        }
    }

    private void updateProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a product to update");
            return;
        }

        try {
            validateInput();
            
            Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(
                "UPDATE products SET barcode = ?, product_name = ?, price = ?, " +
                "stock_quantity = ?, category = ?, description = ? WHERE product_id = ?"
            );
            
            pstmt.setString(1, barcodeField.getText());
            pstmt.setString(2, nameField.getText());
            pstmt.setDouble(3, Double.parseDouble(priceField.getText()));
            pstmt.setInt(4, Integer.parseInt(stockField.getText()));
            pstmt.setString(5, (String) categoryCombo.getSelectedItem());
            pstmt.setString(6, descriptionArea.getText());
            pstmt.setInt(7, (Integer) productTableModel.getValueAt(selectedRow, 0));
            
            pstmt.executeUpdate();
            pstmt.close();
            conn.close();
            
            loadProducts();
            clearForm();
            JOptionPane.showMessageDialog(this, "Product updated successfully!");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating product: " + e.getMessage());
        }
    }

    private void deleteProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a product to delete");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this product?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(
                    "DELETE FROM products WHERE product_id = ?"
                );
                
                pstmt.setInt(1, (Integer) productTableModel.getValueAt(selectedRow, 0));
                pstmt.executeUpdate();
                
                pstmt.close();
                conn.close();
                
                loadProducts();
                clearForm();
                JOptionPane.showMessageDialog(this, "Product deleted successfully!");
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deleting product: " + e.getMessage());
            }
        }
    }

    private void clearForm() {
        barcodeField.setText("");
        nameField.setText("");
        priceField.setText("");
        stockField.setText("");
        descriptionArea.setText("");
        categoryCombo.setSelectedIndex(0);
        productTable.clearSelection();
    }

    private void validateInput() throws Exception {
        if (barcodeField.getText().trim().isEmpty()) {
            throw new Exception("Barcode is required");
        }
        if (nameField.getText().trim().isEmpty()) {
            throw new Exception("Product name is required");
        }
        try {
            double price = Double.parseDouble(priceField.getText());
            if (price < 0) {
                throw new Exception("Price must be positive");
            }
        } catch (NumberFormatException e) {
            throw new Exception("Invalid price format");
        }
        try {
            int stock = Integer.parseInt(stockField.getText());
            if (stock < 0) {
                throw new Exception("Stock quantity must be positive");
            }
        } catch (NumberFormatException e) {
            throw new Exception("Invalid stock quantity format");
        }
    }

    private Connection getConnection() throws Exception {
        String url = "jdbc:mysql://localhost:3306/java_user_database";
        String username = "root";
        String password = "";
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(url, username, password);
    }

    private void generateBarcode() {
        try {
            String newBarcode;
            boolean isUnique = false;
            
            // Keep generating until we find a unique barcode
            do {
                // Generate a random 8-digit number
                int randomNum = 10000000 + random.nextInt(90000000);
                newBarcode = String.valueOf(randomNum);
                
                // Check if this barcode already exists
                Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM products WHERE barcode = ?"
                );
                pstmt.setString(1, newBarcode);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next() && rs.getInt(1) == 0) {
                    isUnique = true;
                }
                
                rs.close();
                pstmt.close();
                conn.close();
                
            } while (!isUnique);
            
            // Set the generated barcode in the field
            barcodeField.setText(newBarcode);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error generating barcode: " + e.getMessage());
        }
    }
} 