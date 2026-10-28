import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class Main {

    static class Expense {
        String description;
        double amount;
        String category;

        Expense(String description, double amount, String category) {
            this.description = description;
            this.amount = amount;
            this.category = category;
        }
    }

    static ArrayList<Expense> expenses = new ArrayList<>();

    static DefaultTableModel model;
    static JLabel totalLabel;
    static JLabel viewTitle;

    static JLabel[] categoryTotals = new JLabel[4];

    static String[] categories = {
        "Personal", "Groceries", "Bills & Fees", "Shopping"
    };

    static String selectedCategory = "All";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createGUI);
    }

    public static void createGUI() {

        JFrame frame = new JFrame("Personal Expense Tracker");
        frame.setSize(1000, 700);
        frame.setMinimumSize(new Dimension(800, 600));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout(10, 10));

        // Main heading
        JLabel title = new JLabel(
            "PERSONAL EXPENSE TRACKER",
            SwingConstants.CENTER
        );

        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setBorder(
            BorderFactory.createEmptyBorder(15, 10, 10, 10)
        );

        frame.add(title, BorderLayout.NORTH);

        // Main content
        JPanel mainPanel = new JPanel(new BorderLayout(10, 15));
        mainPanel.setBorder(
            BorderFactory.createEmptyBorder(5, 15, 10, 15)
        );

        // Four category cards
        JPanel cardsPanel = new JPanel(new GridLayout(1, 4, 12, 12));

        Color[] cardColors = {
            new Color(225, 238, 255),
            new Color(221, 245, 226),
            new Color(255, 237, 210),
            new Color(245, 225, 255)
        };

        for (int i = 0; i < categories.length; i++) {

            final String category = categories[i];

            JPanel card = new JPanel(new BorderLayout(5, 10));
            card.setBackground(cardColors[i]);
            card.setBorder(
                BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(
                        new Color(200, 200, 200)
                    ),
                    BorderFactory.createEmptyBorder(15, 5, 15, 5)
                )
            );

            JLabel categoryName = new JLabel(
                categories[i],
                SwingConstants.CENTER
            );

            categoryName.setFont(
                new Font("Arial", Font.BOLD, 17)
            );

            categoryTotals[i] = new JLabel(
                "₹0.00",
                SwingConstants.CENTER
            );

            categoryTotals[i].setFont(
                new Font("Arial", Font.BOLD, 22)
            );

            JButton viewButton = new JButton("View Expenses");
            viewButton.setFocusPainted(false);

            viewButton.addActionListener(e -> {
                selectedCategory = category;
                refreshTable();
            });

            card.add(categoryName, BorderLayout.NORTH);
            card.add(categoryTotals[i], BorderLayout.CENTER);
            card.add(viewButton, BorderLayout.SOUTH);

            cardsPanel.add(card);
        }

        // Expense entry form
        JPanel formPanel = new JPanel(new GridLayout(2, 4, 10, 10));

        formPanel.setBorder(
            BorderFactory.createTitledBorder("Add New Expense")
        );

        JTextField descriptionField = new JTextField();
        JTextField amountField = new JTextField();

        JComboBox<String> categoryBox =
            new JComboBox<>(categories);

        JButton addButton = new JButton("Add Expense");

        formPanel.add(new JLabel("Description:"));
        formPanel.add(descriptionField);
        formPanel.add(new JLabel("Amount (₹):"));
        formPanel.add(amountField);

        formPanel.add(new JLabel("Category:"));
        formPanel.add(categoryBox);
        formPanel.add(new JLabel(""));
        formPanel.add(addButton);

        // Table heading
        viewTitle = new JLabel(
            "All Expenses",
            SwingConstants.LEFT
        );

        viewTitle.setFont(
            new Font("Arial", Font.BOLD, 19)
        );

        JButton allButton = new JButton("View All Expenses");

        allButton.addActionListener(e -> {
            selectedCategory = "All";
            refreshTable();
        });

        JPanel tableHeading = new JPanel(new BorderLayout(10, 5));
        tableHeading.add(viewTitle, BorderLayout.CENTER);
        tableHeading.add(allButton, BorderLayout.EAST);

        // Expense table
        String[] columns = {
            "Description", "Amount (₹)", "Category"
        };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(27);
        table.setFont(new Font("Arial", Font.PLAIN, 14));

        table.getTableHeader().setFont(
            new Font("Arial", Font.BOLD, 14)
        );

        JScrollPane scrollPane = new JScrollPane(table);

        JPanel tablePanel = new JPanel(new BorderLayout(5, 10));
        tablePanel.add(tableHeading, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Top section: category cards and form
        JPanel topPanel = new JPanel(new BorderLayout(10, 12));
        topPanel.add(cardsPanel, BorderLayout.NORTH);
        topPanel.add(formPanel, BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        // Overall total
        totalLabel = new JLabel("Total Expenses: ₹0.00");

        totalLabel.setFont(
            new Font("Arial", Font.BOLD, 20)
        );

        totalLabel.setBorder(
            BorderFactory.createEmptyBorder(10, 5, 10, 5)
        );

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(totalLabel, BorderLayout.WEST);

        frame.add(mainPanel, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        // Add expense functionality
        addButton.addActionListener(e -> {

            String description =
                descriptionField.getText().trim();

            String amountText =
                amountField.getText().trim();

            String category =
                (String) categoryBox.getSelectedItem();

            if (description.isEmpty() || amountText.isEmpty()) {
                JOptionPane.showMessageDialog(
                    frame,
                    "Please enter both description and amount."
                );
                return;
            }

            try {
                double amount = Double.parseDouble(amountText);

                if (!Double.isFinite(amount) || amount <= 0) {
                    JOptionPane.showMessageDialog(
                        frame,
                        "Enter an amount greater than zero."
                    );
                    return;
                }

                expenses.add(
                    new Expense(description, amount, category)
                );

                descriptionField.setText("");
                amountField.setText("");

                selectedCategory = "All";
                refreshTable();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                    frame,
                    "Please enter a valid numeric amount."
                );
            }
        });

        refreshTable();
        frame.setVisible(true);
    }

    static void refreshTable() {

        model.setRowCount(0);

        double grandTotal = 0;

        double[] categoryAmounts = new double[4];

        for (Expense expense : expenses) {

            grandTotal += expense.amount;

            for (int i = 0; i < categories.length; i++) {
                if (expense.category.equals(categories[i])) {
                    categoryAmounts[i] += expense.amount;
                }
            }

            if (selectedCategory.equals("All")
                    || expense.category.equals(selectedCategory)) {

                model.addRow(new Object[] {
                    expense.description,
                    String.format("%.2f", expense.amount),
                    expense.category
                });
            }
        }

        // Update all four category cards
        for (int i = 0; i < categories.length; i++) {
            categoryTotals[i].setText(
                String.format("₹%.2f", categoryAmounts[i])
            );
        }

        // Update overall total
        totalLabel.setText(
            String.format("Total Expenses: ₹%.2f", grandTotal)
        );

        // Update table heading
        if (selectedCategory.equals("All")) {
            viewTitle.setText("All Expenses");
        } else {
            viewTitle.setText(selectedCategory + " Expenses");
        }
    }
}
