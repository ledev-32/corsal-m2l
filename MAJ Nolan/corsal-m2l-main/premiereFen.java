package btssio.remis.gestionAdLaurent;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;

public class premiereFen extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtNom, txtPrenom;
    private JComboBox<String> comboCategorie;
    private JTextArea textArea;
    private static final String FILE_PATH = "adherents.xml";
    


    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                premiereFen frame = new premiereFen();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public premiereFen() {
        setTitle("Gestion des Adhérents");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 500, 400);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblTitre = new JLabel("Gestion des Adhérents");
        lblTitre.setBounds(180, 10, 200, 25);
        contentPane.add(lblTitre);

        JLabel lblNom = new JLabel("Nom:");
        lblNom.setBounds(50, 60, 100, 20);
        contentPane.add(lblNom);
        
        txtNom = new JTextField();
        txtNom.setBounds(150, 60, 200, 20);
        contentPane.add(txtNom);
        
        JLabel lblPrenom = new JLabel("Prénom:");
        lblPrenom.setBounds(50, 100, 100, 20);
        contentPane.add(lblPrenom);
        
        txtPrenom = new JTextField();
        txtPrenom.setBounds(150, 100, 200, 20);
        contentPane.add(txtPrenom);
        
        JLabel lblCategorie = new JLabel("Catégorie:");
        lblCategorie.setBounds(50, 140, 100, 20);
        contentPane.add(lblCategorie);
        
        comboCategorie = new JComboBox<>();
        comboCategorie.setBounds(150, 140, 200, 20);
        contentPane.add(comboCategorie);
        
        JButton btnAfficher = new JButton("Afficher Adhérents");
        btnAfficher.setBounds(140, 220, 200, 30);
        contentPane.add(btnAfficher);
        btnAfficher.addActionListener(e -> new FenetreAffichage().setVisible(true));
    }
    
    class FenetreAffichage extends JFrame {
        private JTable table;
        private JScrollPane scrollPane;
        
        public FenetreAffichage() {
            setTitle("Liste des Adhérents");
            setBounds(200, 200, 500, 300);
            setLayout(new BorderLayout());
            
            String[] columnNames = {"Nom", "Prénom", "Catégorie"};
            String[][] data = chargerAdherents();
            
            table = new JTable(data, columnNames);
            scrollPane = new JScrollPane(table);
            add(scrollPane, BorderLayout.CENTER);
            
            setVisible(true);
        }

        private String[][] chargerAdherents() {
            File file = new File(FILE_PATH);
            if (!file.exists() || file.length() == 0) {
                return new String[][] {{"Aucun adhérent", "", ""}};
            }
            
            try {
                Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
                NodeList nList = document.getElementsByTagName("adherent");
                String[][] data = new String[nList.getLength()][3];
                
                for (int i = 0; i < nList.getLength(); i++) {
                    Element element = (Element) nList.item(i);
                    data[i][0] = element.getElementsByTagName("nom").item(0).getTextContent();
                    data[i][1] = element.getElementsByTagName("prenom").item(0).getTextContent();
                    data[i][2] = element.getElementsByTagName("categorie").item(0).getTextContent();
                }
                return data;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new String[][] {{"Erreur", "", ""}};
        }
    }

    private void supprimerAdherentXML(String nom, String prenom) {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) return;

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);

            NodeList adherents = document.getElementsByTagName("adherent");
            for (int i = 0; i < adherents.getLength(); i++) {
                Element adherent = (Element) adherents.item(i);
                String nomAdh = adherent.getElementsByTagName("nom").item(0).getTextContent();
                String prenomAdh = adherent.getElementsByTagName("prenom").item(0).getTextContent();
                
                if (nomAdh.equals(nom) && prenomAdh.equals(prenom)) {
                    adherent.getParentNode().removeChild(adherent);
                    break;
                }
            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(document), new StreamResult(new FileWriter(FILE_PATH)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ajouterAdherentXML(String nom, String prenom, String categorie) {
        try {
            File file = new File(FILE_PATH);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document;

            if (file.exists() && file.length() > 0) {
                document = builder.parse(file);
            } else {
                document = builder.newDocument();
                Element root = document.createElement("adherents");
                document.appendChild(root);
            }

            Element root = document.getDocumentElement();
            Element adherent = document.createElement("adherent");

            adherent.appendChild(createElement(document, "nom", nom));
            adherent.appendChild(createElement(document, "prenom", prenom));
            adherent.appendChild(createElement(document, "categorie", categorie));
            root.appendChild(adherent);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(document), new StreamResult(new FileWriter(FILE_PATH)));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private Element createElement(Document doc, String tagName, String textContent) {
        Element element = doc.createElement(tagName);
        element.appendChild(doc.createTextNode(textContent));
        return element;
    }

    private void chargerCategories() {
        try {
            File file = new File("categorie.xml");
            if (!file.exists()) return;
            
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);
            document.getDocumentElement().normalize();
            
            NodeList nodeList = document.getElementsByTagName("categorie");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    comboCategorie.addItem(element.getTextContent());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class FenetreModification extends JFrame {
        public FenetreModification() {
            setTitle("Modifier un Adhérent");
            setBounds(200, 200, 400, 300);
            setVisible(true);
        }
    }
}
