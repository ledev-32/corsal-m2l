package btssio.remis.gestionAdLaurent;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class ClubRegion {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClubRegion().createAndShowGUI());
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Clubs Escrime");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        String[] columnNames = {"ID", "Nom", "Adresse", "Contact", "Téléphone", "Mail", "Site Web"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        JButton exportButton = new JButton("Exporter en PDF");
        exportButton.addActionListener(e -> exportToPDF(table));

        JPanel panel = new JPanel();
        panel.add(exportButton);

        frame.add(scrollPane, "Center");
        frame.add(panel, "South");
        frame.setSize(800, 400);
        frame.setVisible(true);
        
        loadXMLData("club.xml", model);
    }

    private void loadXMLData(String filePath, DefaultTableModel model) {
        try {
            File xmlFile = new File(filePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList clubList = doc.getElementsByTagName("club");
            for (int i = 0; i < clubList.getLength(); i++) {
                Node node = clubList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String id = element.getAttribute("id");
                    String nom = element.getElementsByTagName("nom").item(0).getTextContent();
                    String adresse = element.getElementsByTagName("adresse").item(0).getTextContent();
                    String contact = element.getElementsByTagName("contact").item(0).getTextContent();
                    String tel = element.getElementsByTagName("tel").item(0).getTextContent();
                    String mail = element.getElementsByTagName("mail").item(0).getTextContent();
                    String site = element.getElementsByTagName("site").item(0).getTextContent();

                    model.addRow(new Object[]{id, nom, adresse, contact, tel, mail, site});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur lors du chargement du fichier XML", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportToPDF(JTable table) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream("clubs.pdf"));
            document.open();
            PdfPTable pdfTable = new PdfPTable(table.getColumnCount());
            
            for (int i = 0; i < table.getColumnCount(); i++) {
                pdfTable.addCell(table.getColumnName(i));
            }
            
            for (int rows = 0; rows < table.getRowCount(); rows++) {
                for (int cols = 0; cols < table.getColumnCount(); cols++) {
                    pdfTable.addCell(table.getValueAt(rows, cols).toString());
                }
            }
            
            document.add(pdfTable);
            document.close();
            JOptionPane.showMessageDialog(null, "PDF exporté avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur lors de l'exportation du PDF", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
