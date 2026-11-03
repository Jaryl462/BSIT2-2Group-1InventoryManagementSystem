/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ds.trys;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Rudy Anonuevo
 */
public class SupplierFrame extends JFrame implements ActionListener {
    private final InventoryManager manager;
    private DefaultTableModel supplierModel;
    private JTable supplierTable;
    private JScrollPane supplierScroll;
    private JTextField txtSID, txtName, txtContact;
    private JLabel lbSupSID, lbSupName, lbSupContact;
    private JButton btnAddSupplier, btnUpdateSupplier, btnDeleteSupplier, btnRefreshSupplier;

    SupplierFrame(InventoryManager manager) {
        this.manager = manager;
        String[] columns = {"Supplier ID", "Supplier Name", "Contact Info"};
        supplierModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        setTitle(manager.isAdmin() ? "Manage Suppliers" : "View Suppliers [Staff - View Only]");
        setSize(650, 400);
        setLayout(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        supplierTable = new JTable(supplierModel);
        supplierScroll = new JScrollPane(supplierTable);
        supplierScroll.setBounds(10, 10, 615, 200);
        add(supplierScroll);

        lbSupSID = new JLabel("Supplier ID:");
        lbSupSID.setBounds(10, 225, 80, 30);
        add(lbSupSID);

        txtSID = new JTextField();
        txtSID.setBounds(90, 225, 100, 30);
        add(txtSID);

        lbSupName = new JLabel("Name:");
        lbSupName.setBounds(205, 225, 45, 30);
        add(lbSupName);

        txtName = new JTextField();
        txtName.setBounds(250, 225, 150, 30);
        add(txtName);

        lbSupContact = new JLabel("Contact:");
        lbSupContact.setBounds(415, 225, 60, 30);
        add(lbSupContact);

        txtContact = new JTextField();
        txtContact.setBounds(475, 225, 150, 30);
        add(txtContact);

        btnAddSupplier = new JButton("Add Supplier");
        btnAddSupplier.setBounds(10, 280, 130, 30);
        add(btnAddSupplier);

        btnUpdateSupplier = new JButton("Update Supplier");
        btnUpdateSupplier.setBounds(150, 280, 140, 30);
        add(btnUpdateSupplier);

        btnDeleteSupplier = new JButton("Delete Supplier");
        btnDeleteSupplier.setBounds(300, 280, 140, 30);
        add(btnDeleteSupplier);

        btnRefreshSupplier = new JButton("Refresh");
        btnRefreshSupplier.setBounds(450, 280, 100, 30);
        add(btnRefreshSupplier);

        btnAddSupplier.addActionListener(this);
        btnUpdateSupplier.addActionListener(this);
        btnDeleteSupplier.addActionListener(this);
        btnRefreshSupplier.addActionListener(this);

        if (!manager.isAdmin()) {
            btnAddSupplier.setEnabled(false);
            btnUpdateSupplier.setEnabled(false);
            btnDeleteSupplier.setEnabled(false);
            txtSID.setEnabled(false);
            txtName.setEnabled(false);
            txtContact.setEnabled(false);
        }

        manager.loadDataFromDatabase();
        supplierModel.setRowCount(0);
        for (Supplier s : manager.getSuppliers()) {
            supplierModel.addRow(new Object[]{s.getId(), s.getName(), s.getContactInfo()});
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnRefreshSupplier) {
            manager.loadDataFromDatabase();
            supplierModel.setRowCount(0);
            for (Supplier s : manager.getSuppliers()) {
                supplierModel.addRow(new Object[]{s.getId(), s.getName(), s.getContactInfo()});
            }
            return;
        }
        if (!manager.isAdmin()) return;

        if (e.getSource() == btnAddSupplier) {
            String SID = txtSID.getText().trim();
            String sName = txtName.getText().trim();
            String sContact = txtContact.getText().trim();
            if (!SID.isEmpty() && !sName.isEmpty() && !sContact.isEmpty()) {
                try {
                    manager.addSupplier(new Supplier(SID, sName, sContact));
                    supplierModel.setRowCount(0);
                    for (Supplier s : manager.getSuppliers()) {
                        supplierModel.addRow(new Object[]{s.getId(), s.getName(), s.getContactInfo()});
                    }
                    txtSID.setText("");
                    txtName.setText("");
                    txtContact.setText("");
                    JOptionPane.showMessageDialog(this, "Supplier added to database successfully!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please enter all supplier details first", "ERROR Message", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == btnUpdateSupplier) {
            int selectedRow = supplierTable.getSelectedRow();
            if (selectedRow != -1) {
                String sName = txtName.getText().trim();
                String sContact = txtContact.getText().trim();
                if (!sName.isEmpty() && !sContact.isEmpty()) {
                    String SID = supplierModel.getValueAt(selectedRow, 0).toString();
                    try {
                        if (manager.updateSupplier(SID, sName, sContact)) {
                            supplierModel.setRowCount(0);
                            for (Supplier s : manager.getSuppliers()) {
                                supplierModel.addRow(new Object[]{s.getId(), s.getName(), s.getContactInfo()});
                            }
                            txtSID.setText("");
                            txtName.setText("");
                            txtContact.setText("");
                            JOptionPane.showMessageDialog(this, "Supplier updated in database successfully!");
                        } else {
                            supplierModel.setRowCount(0);
                            for (Supplier s : manager.getSuppliers()) {
                                supplierModel.addRow(new Object[]{s.getId(), s.getName(), s.getContactInfo()});
                            }
                            JOptionPane.showMessageDialog(this, "Supplier no longer exists.", "Update Failed", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Please enter the new name and contact info first", "ERROR Message", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a supplier from the table first", "ERROR Message", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == btnDeleteSupplier) {
            int selectedRow = supplierTable.getSelectedRow();
            if (selectedRow != -1) {
                int confirm = JOptionPane.showConfirmDialog(this, "Delete the selected supplier?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    String SID = supplierModel.getValueAt(selectedRow, 0).toString();
                    try {
                        manager.deleteSupplier(SID);
                        supplierModel.setRowCount(0);
                        for (Supplier s : manager.getSuppliers()) {
                            supplierModel.addRow(new Object[]{s.getId(), s.getName(), s.getContactInfo()});
                        }
                        JOptionPane.showMessageDialog(this, "Supplier deleted from database successfully!");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage(), "Cannot Delete", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a supplier from the table first", "ERROR Message", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}