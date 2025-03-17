package btssio.remis.gestionAdLaurent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import org.w3c.dom.*;

public class premiereFen extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtNom, txtNomNaissance, txtPrenom, txtDateNaissance, txtVilleNaissance, txtNationalite;
    private JTextField txtAdresse, txtCodePostal, txtVille, txtTel1, txtTel2, txtEmail;
    private JTextArea textArea;
    private JComboBox<String> comboGenre, comboCategorie;
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
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        contentPane = new JPanel(new GridBagLayout());
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        String[] labels = {"Nom:", "Nom de naissance:", "Prénom:", "Genre:", "Date de naissance:", "Ville de naissance:", "Nationalité:", "Adresse:", "Code Postal:", "Ville:", "Téléphone 1:", "Téléphone 2:", "Email:", "Catégorie:"};
        JComponent[] inputs = {
            txtNom = new JTextField(),
            txtNomNaissance = new JTextField(),
            txtPrenom = new JTextField(),
            comboGenre = new JComboBox<>(new String[]{"Masculin", "Féminin"}),
            txtDateNaissance = new JTextField(),
            txtVilleNaissance = new JTextField(),
            txtNationalite = new JTextField(),
            txtAdresse = new JTextField(),
            txtCodePostal = new JTextField(),
            txtVille = new JTextField(),
            txtTel1 = new JTextField(),
            txtTel2 = new JTextField(),
            txtEmail = new JTextField(),
            comboCategorie = new JComboBox<>()
        };
        
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            contentPane.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            contentPane.add(inputs[i], gbc);
        }
        
        gbc.gridx = 0;
        gbc.gridy = labels.length;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        textArea = new JTextArea(5, 40);
        textArea.setEditable(false);
        contentPane.add(new JScrollPane(textArea), gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnAjouter = new JButton("Ajouter");
        JButton btnModifier = new JButton("Modifier");
        JButton btnSupprimer = new JButton("Supprimer");
        JButton btnAfficher = new JButton("Afficher Adhérents");
        buttonPanel.add(btnAjouter);
        buttonPanel.add(btnModifier);
        buttonPanel.add(btnSupprimer);
        buttonPanel.add(btnAfficher);
        
        gbc.gridy = labels.length + 1;
        contentPane.add(buttonPanel, gbc);
        
        btnAjouter.addActionListener(e -> {
            ajouterAdherentXML(txtNom.getText(), txtNomNaissance.getText(), txtPrenom.getText(), (String) comboGenre.getSelectedItem(), txtDateNaissance.getText(), txtVilleNaissance.getText(), txtNationalite.getText(), txtAdresse.getText(), txtCodePostal.getText(), txtVille.getText(), txtTel1.getText(), txtTel2.getText(), txtEmail.getText(), (String) comboCategorie.getSelectedItem());
            afficherAdherents();
        });
        
        btnAfficher.addActionListener(e -> new FenetreAffichage().setVisible(true));
        chargerCategories();
        afficherAdherents();
    }

    private void afficherAdherents() {
        textArea.setText("");
        File file = new File(FILE_PATH);
        if (!file.exists() || file.length() == 0) {
            return;
        }

        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
            NodeList nList = document.getElementsByTagName("adherent");
            for (int i = 0; i < nList.getLength(); i++) {
                Element element = (Element) nList.item(i);
                textArea.append(element.getElementsByTagName("nom").item(0).getTextContent() + " " +
                        element.getElementsByTagName("prenom").item(0).getTextContent() + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ajouterAdherentXML(String nom, String nomNaissance, String prenom, String genre, String dateNaissance, String villeNaissance, String nationalite, String adresse, String codeP, String ville, String telUn, String telDeux, String email, String categorie) {
        try {
            File file = new File(FILE_PATH);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = file.exists() ? builder.parse(file) : builder.newDocument();
            Element root = document.getDocumentElement();
            if (root == null) {
                root = document.createElement("adherents");
                document.appendChild(root);
            }

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
}
