package org.fit.layout.storage.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ConnectDialog extends JDialog
{
    private static final long serialVersionUID = 1L;

    private String result = null;
    
    private final JPanel contentPanel = new JPanel();
    private JTextField txtEndpointurl;

    public static String show(String value)
    {
        try
        {
            ConnectDialog dialog = new ConnectDialog();
            dialog.txtEndpointurl.setText(value);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
            return dialog.result;
        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Create the dialog.
     */
    public ConnectDialog()
    {
        setTitle("RDF Repository Connection");
        setModal(true);
        setBounds(100, 100, 450, 161);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        GridBagLayout gbl_contentPanel = new GridBagLayout();
        gbl_contentPanel.columnWeights = new double[]{1.0};
        gbl_contentPanel.rowWeights = new double[]{0.0, 0.0};
        contentPanel.setLayout(gbl_contentPanel);
        {
            JLabel lblEndpointUrl = new JLabel("Endpoint URL");
            GridBagConstraints gbc_lblEndpointUrl = new GridBagConstraints();
            gbc_lblEndpointUrl.insets = new Insets(0, 0, 5, 5);
            gbc_lblEndpointUrl.anchor = GridBagConstraints.WEST;
            gbc_lblEndpointUrl.gridx = 0;
            gbc_lblEndpointUrl.gridy = 0;
            contentPanel.add(lblEndpointUrl, gbc_lblEndpointUrl);
        }
        {
            txtEndpointurl = new JTextField();
            GridBagConstraints gbc_txtEndpointurl = new GridBagConstraints();
            gbc_txtEndpointurl.insets = new Insets(0, 0, 0, 5);
            gbc_txtEndpointurl.fill = GridBagConstraints.HORIZONTAL;
            gbc_txtEndpointurl.gridx = 0;
            gbc_txtEndpointurl.gridy = 1;
            contentPanel.add(txtEndpointurl, gbc_txtEndpointurl);
            txtEndpointurl.setColumns(10);
        }
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("OK");
                okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        result = txtEndpointurl.getText();
                        setVisible(false);
                        dispose();
                    }
                });
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            {
                JButton cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        result = null;
                        setVisible(false);
                        dispose();
                    }
                });
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }
        }
    }
}
