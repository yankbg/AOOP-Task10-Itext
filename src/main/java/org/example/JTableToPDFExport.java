package org.example;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.*;

public class JTableToPDFExport extends JFrame {
    // GUI components
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton loadDataBtn, exportPdfBtn;
    // constructor
    public JTableToPDFExport() {
        super("JTable to PDF Export Example");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Table setup
        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Buttons panel
        JPanel buttonPanel = new JPanel();
        loadDataBtn = new JButton("Load Data");
        exportPdfBtn = new JButton("Export to PDF");
        buttonPanel.add(loadDataBtn);
        buttonPanel.add(exportPdfBtn);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load data button action - loads from DB
        loadDataBtn.addActionListener(e -> loadDataFromDatabase());

        // Export to PDF button action
        exportPdfBtn.addActionListener(e -> exportTableDataToPDF());

        setVisible(true);
    }
    private void loadDataFromDatabase() {
        // Example DB connection and query, modify as needed
        String url = "jdbc:mysql://localhost:3306/student01";
        String user = "root";
        String password = "1234yan#$";
        String query = "SELECT id, name, grade FROM students";

        try (Connection con = DriverManager.getConnection(url, user, password);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Clear existing data
            tableModel.setRowCount(0);
            // Get column metadata and setup table columns dynamically
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Set column names to table model
            String[] columnNames = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                columnNames[i - 1] = metaData.getColumnName(i);
            }
            tableModel.setColumnIdentifiers(columnNames);

            // Add rows to table model
            while (rs.next()) {
                Object[] rowData = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    rowData[i - 1] = rs.getObject(i);
                }
                tableModel.addRow(rowData);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void exportTableDataToPDF() {
        // Check if table has data
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No data to export.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // File chooser for saving PDF
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save PDF Report");
        fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));
        int userSelection = fileChooser.showSaveDialog(this);

        // If user approves file selection
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".pdf")) {
                filePath += ".pdf";
            }

            Document document = new Document(PageSize.A4);
            try {
                PdfWriter.getInstance(document, new java.io.FileOutputStream(filePath));
                document.open();

                // Title with bold, larger font
                Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
                Paragraph title = new Paragraph("FACULTY OF SCIENCE AND TECHNOLOGY\n" +
                        "ADVANCED OBJECT-ORIENTED PROGRAMMING CLASS ACTIVITY\n\nAdvanced Object Oriented Programming\n\n", titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                document.add(title);

                // Date timestamp
                String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                Paragraph timestamp = new Paragraph("Report generated on: " + date + "\n\n");
                timestamp.setAlignment(Element.ALIGN_RIGHT);
                document.add(timestamp);

                // Create PDF table with same column count as JTable
                PdfPTable pdfTable = new PdfPTable(tableModel.getColumnCount());
                pdfTable.setWidthPercentage(100);
                pdfTable.setSpacingBefore(10f);
                pdfTable.setSpacingAfter(10f);

                // Set column widths proportional if needed
                float[] columnWidths = new float[tableModel.getColumnCount()];
                for (int i = 0; i < columnWidths.length; i++) {
                    columnWidths[i] = 4f; // equal widths example
                }
                pdfTable.setWidths(columnWidths);

                // Header styling
                Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    PdfPCell headerCell = new PdfPCell(new Phrase(tableModel.getColumnName(i), headerFont));
                    headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    headerCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    headerCell.setBorderWidth(1);
                    pdfTable.addCell(headerCell);
                }

                // Table data cells with borders and alignment
                Font dataFont = new Font(Font.FontFamily.HELVETICA, 11);
                for (int row = 0; row < tableModel.getRowCount(); row++) {
                    for (int col = 0; col < tableModel.getColumnCount(); col++) {
                        Object value = tableModel.getValueAt(row, col);
                        PdfPCell cell = new PdfPCell(new Phrase(value == null ? "" : value.toString(), dataFont));
                        cell.setPadding(5);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setBorderWidth(1);
                        pdfTable.addCell(cell);
                    }
                }

                // Add table to document
                document.add(pdfTable);
                document.close();
                JOptionPane.showMessageDialog(this, "PDF report generated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error generating PDF: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
