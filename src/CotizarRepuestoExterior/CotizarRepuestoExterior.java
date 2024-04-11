package CotizarRepuestoExterior;

import java.awt.Color;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import Clases.RepuestoExterior;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import Clases.PDFData;
import Clases.Repuesto;
import DAO.RepuestoCotizado_DAO;
import DAO.Repuesto_DAO;
import PDFGenerator.PDGenerator;
import textfield_suggestion.TextFieldSuggestion;
import java.awt.Font;
import combobox.Combobox;
import tabledark.TableDark;
import button.MyButton;

public class CotizarRepuestoExterior extends JPanel {

	private TextFieldSuggestion Cantidad = new TextFieldSuggestion();
	private Repuesto repuesto;
	private ArrayList<Repuesto> repuestos = new ArrayList<>();
	private ArrayList<RepuestoExterior> repuestosCotizados = new ArrayList<>();
	private Repuesto_DAO repdao = new Repuesto_DAO();
	private TextFieldSuggestion NumeroDeParte = new TextFieldSuggestion();
	private TextFieldSuggestion Existencias = new TextFieldSuggestion();
	private TextFieldSuggestion Descripcion = new TextFieldSuggestion();
	private TextFieldSuggestion Precio = new TextFieldSuggestion();
	private Combobox<String> combobox = new Combobox<String>();
	private DefaultComboBoxModel<String> comboBoxModel;
	private static final long serialVersionUID = 1L;
	private TextFieldSuggestion Buscar = new TextFieldSuggestion();
	private TextFieldSuggestion PrecioTotal = new TextFieldSuggestion();

	private BigDecimal precioTotalRepuestos = BigDecimal.ZERO;
	private TextFieldSuggestion PrecioTotalRepuestos = new TextFieldSuggestion();
	private TextFieldSuggestion Fecha = new TextFieldSuggestion();

	private JLabel lblNumeroDeParte = new JLabel();
	private JLabel lblExistencias = new JLabel();
	private JLabel lblDescripcion = new JLabel();
	private JLabel lblPrecio = new JLabel();
	private JLabel lblPrecioTotal = new JLabel();
	private MyButton CotizarBtn = new MyButton();

	DefaultTableModel model = new DefaultTableModel(new Object[][] {},
			new String[] { "P/N", "DESCRIPCION", "P. UNITARIO", "CANT.", "P. TOTAL" });

	private RepuestoExterior repuestoCotizado = new RepuestoExterior();
	private JLabel IngreseLaCantidad = new JLabel();
	private MyButton mbtnDarBaja = new MyButton();
	private JLabel lblCantidad = new JLabel();
	private JLabel lblPrecioTotalCotizacion = new JLabel();
	private JLabel lblCotizacionExterior = new JLabel();
	private BigDecimal precioTotal;
	private BigDecimal precioExterior;

	private Date currentDate = new Date();
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	private String formattedDate;
	private TextFieldSuggestion NombreCliente = new TextFieldSuggestion();
	private TextFieldSuggestion CondicionDePago = new TextFieldSuggestion();
	private TextFieldSuggestion ValidezCotizacion = new TextFieldSuggestion();
	private TextFieldSuggestion Atte = new TextFieldSuggestion();

	private Object[] message = { "Cliente:", NombreCliente, "Condicion de pago:", CondicionDePago,
			"Validez cotizacion:", ValidezCotizacion, "Fecha:", Fecha, "Atte.:", Atte };

	private BigDecimal cantidad;

	private BigDecimal precioRepuesto;
	private PDFData data = new PDFData();
	private MyButton reset = new MyButton();
	private RepuestoCotizado_DAO repcotdaoo = new RepuestoCotizado_DAO();

	public CotizarRepuestoExterior() {

		Timer timer1 = new Timer(5000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.setRowCount(0);
				updatePanel();

			}
		});

		// Inicia el Timer
		timer1.start();

		Precio.setText("PRECIO EXTERIOR");
		Precio.setRound(0);
		Precio.setForeground(Color.WHITE);
		Precio.setEnabled(true);
		Precio.setEditable(false);
		Precio.setBounds(422, 148, 178, 34);
		Precio.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				Precio.setText(null);
			}
		});

		Precio.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				while (Precio.getText().isEmpty())
					Precio.setText("PRECIO EXTERIOR");
			}

		});
		add(Precio);

		Descripcion.setEditable(false);

		Descripcion.setText("DESCRIPCION");
		Descripcion.setRound(0);
		Descripcion.setForeground(Color.WHITE);
		Descripcion.setBounds(147, 148, 178, 34);
		add(Descripcion);
		Existencias.setEditable(false);

		Existencias.setText("EXISTENCIAS");
		Existencias.setRound(0);
		Existencias.setForeground(Color.WHITE);
		Existencias.setBounds(422, 91, 178, 34);
		add(Existencias);
		NumeroDeParte.setEditable(false);

		NumeroDeParte.setText("NUMERO DE PARTE");
		NumeroDeParte.setRound(0);
		NumeroDeParte.setForeground(Color.WHITE);
		NumeroDeParte.setBounds(147, 91, 178, 34);
		add(NumeroDeParte);

		setBackground(new Color(29, 29, 29));
		setLayout(null);

		JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
		jScrollPane1.setBounds(636, 7, 535, 293);
		jScrollPane1.setBorder(BorderFactory.createEmptyBorder());
		jScrollPane1.setViewportBorder(BorderFactory.createEmptyBorder());

		tableDark1.setEnabled(false);
		tableDark1.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		tableDark1.setBorder(null);
		tableDark1.setForeground(new Color(192, 192, 192));
		tableDark1.setAutoCreateRowSorter(true);
		TableRowSorter<DefaultTableModel> sorter;
		model.setRowCount(0);
		repuestosCotizados = repcotdaoo.consultarTodosLosRepuestosExteriores();
		Object[] fila = new Object[model.getColumnCount()];
		for (RepuestoExterior p : repuestosCotizados) {
			fila[0] = p.getRepuesto().getNumeroDeParte();
			fila[1] = p.getRepuesto().getDescripcion();
			fila[2] = p.getPrecioExterior();
			fila[3] = p.getCantidad();
			fila[4] = p.getPrecioTotal();
			model.addRow(fila);
			precioTotalRepuestos = precioTotalRepuestos.add(p.getPrecioTotal());
			tableDark1.validate();
			model.fireTableDataChanged();
		}

		tableDark1.setModel(model);
		sorter = new TableRowSorter<>(model);
		tableDark1.setRowSorter(sorter);
		for (int row = 0; row < model.getRowCount(); row++) {
			for (int col = 0; col < model.getColumnCount(); col++) {
				Object value = model.getValueAt(row, col);
				String text = (value != null) ? value.toString() : "";
				if (col == 2 || col == 4) {

					text = "USD " + text;
					model.setValueAt(text, row, col);

				}

			}

		}
		jScrollPane1.setViewportView(tableDark1);
		jScrollPane1.getViewport().setBackground(new Color(29, 29, 29));
		add(jScrollPane1);

		repuestos = repdao.consultarTodosLosRepuestos();
		setBackground(new Color(29, 29, 29));
		setLayout(null);

		comboBoxModel = new DefaultComboBoxModel<>();
		combobox.setModel(comboBoxModel);

		for (Repuesto e : repuestos) {
			String value = e.getNumeroDeParte();
			combobox.addItem(value);
		}
		combobox.setLineColor(new Color(1, 179, 169));
		combobox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (combobox.getSelectedItem() != null && Buscar.getText() != null) {

					String numparte = combobox.getSelectedItem().toString();
					repuesto = repdao.consultarRepuesto(repdao.consultarIdRepuesto(numparte));

					NumeroDeParte.setText(repuesto.getNumeroDeParte());
					NumeroDeParte.setEnabled(true);
					NumeroDeParte.setEditable(false);
					Existencias.setText(Integer.toString(repuesto.getExistencia()));
					Existencias.setEnabled(true);

					Existencias.setEditable(false);
					Descripcion.setText(repuesto.getDescripcion());
					Descripcion.setEnabled(true);

					Descripcion.setEditable(false);
					Precio.setText(repuesto.getPrecioExterior().toString());
					Precio.setEnabled(true);
					Precio.setEditable(false);
				}
			}
		});

		combobox.setEnabled(true);
		combobox.setEditable(false);
		combobox.setForeground(Color.WHITE);
		combobox.setBackground(new Color(29, 29, 29));

		combobox.setLabeText("Numero de parte");
		combobox.setBounds(422, 6, 166, 34);

		add(combobox);

		lblNumeroDeParte.setText("Numero de parte");
		lblNumeroDeParte.setForeground(Color.WHITE);
		lblNumeroDeParte.setFont(new Font("SansSerif", Font.PLAIN, 12));
		lblNumeroDeParte.setBounds(35, 76, 102, 19);
		add(lblNumeroDeParte);

		lblExistencias.setText("Existencias");
		lblExistencias.setForeground(Color.WHITE);
		lblExistencias.setFont(new Font("SansSerif", Font.PLAIN, 12));
		lblExistencias.setBounds(335, 76, 77, 19);
		add(lblExistencias);

		lblDescripcion.setText("Descripcion");
		lblDescripcion.setForeground(Color.WHITE);
		lblDescripcion.setFont(new Font("SansSerif", Font.PLAIN, 12));
		lblDescripcion.setBounds(35, 136, 106, 19);
		add(lblDescripcion);

		lblPrecio.setText("Precio Exterior");
		lblPrecio.setForeground(Color.WHITE);
		lblPrecio.setFont(new Font("SansSerif", Font.PLAIN, 12));
		lblPrecio.setBounds(332, 136, 87, 19);
		add(lblPrecio);

		lblCantidad.setText("Cantidad");
		lblCantidad.setForeground(Color.WHITE);
		lblCantidad.setFont(new Font("SansSerif", Font.PLAIN, 12));
		lblCantidad.setBounds(60, 258, 62, 19);
		add(lblCantidad);
		Cantidad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (Cantidad.getText() != null) {
					PrecioTotal.setText(precioTotal().toString());
				}
			}
		});

		Cantidad.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				while (Cantidad.getText() == null)
					PrecioTotal.setText("PRECIO TOTAL");
				Cantidad.setText("CANTIDAD");
			}
		});
		Cantidad.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Cantidad.setText(null);
			}
		});
		Cantidad.setText("CANTIDAD");
		Cantidad.setRound(0);
		Cantidad.setForeground(Color.WHITE);
		Cantidad.setBounds(147, 266, 178, 34);
		Cantidad.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				updatePriceTotal();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				updatePriceTotal();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				updatePriceTotal();
			}
		});
		add(Cantidad);
		PrecioTotal.setEditable(false);
		PrecioTotal.setText("USD 0");
		PrecioTotal.setRound(0);
		PrecioTotal.setForeground(Color.WHITE);
		PrecioTotal.setBounds(147, 321, 178, 34);
		add(PrecioTotal);

		lblPrecioTotal.setText("Precio Total");
		lblPrecioTotal.setForeground(Color.WHITE);
		lblPrecioTotal.setFont(new Font("SansSerif", Font.PLAIN, 12));
		lblPrecioTotal.setBounds(60, 311, 77, 19);
		add(lblPrecioTotal);

		CotizarBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (Integer.parseInt(Cantidad.getText()) <= Integer.parseInt(Existencias.getText())
							&& Integer.parseInt(Cantidad.getText()) > 0) {
						if (Cantidad.getText() != null && PrecioTotal.getText() != null) {
							repuestoCotizado.setCantidad(Integer.parseInt(Cantidad.getText()));
							precioTotal = new BigDecimal(PrecioTotal.getText());
							repuestoCotizado.setPrecioTotal(precioTotal);
							precioExterior = new BigDecimal(Precio.getText());
							repuestoCotizado.setPrecioExterior(precioExterior);
							repuestoCotizado.setRepuesto(repuesto);
							repuestoCotizado.setIdRepuesto();
							repuestoCotizado.setTipo();
							repcotdaoo.agregarRespuestoExterior(repuestoCotizado);
							repaint();

						}
					} else {

						if (Cantidad.getText() != null && PrecioTotal.getText() != null) {

							int sinStock = Integer.parseInt(Existencias.getText())
									- Integer.parseInt(Cantidad.getText());

							repuestoCotizado.setCantidad(Integer.parseInt(Cantidad.getText()));
							precioTotal = new BigDecimal(PrecioTotal.getText());
							repuestoCotizado.setPrecioTotal(precioTotal);
							precioExterior = new BigDecimal(Precio.getText());
							repuestoCotizado.setPrecioExterior(precioExterior);
							repuestoCotizado.setRepuesto(repuesto);
							repuestoCotizado.setIdRepuesto();
							repuestoCotizado.setTipo();
							repcotdaoo.agregarRespuestoExterior(repuestoCotizado);
							JOptionPane.showMessageDialog(null,
									"NO HAY SUFICIENTE STOCK, EXISTENCIA RESTANTE: " + sinStock, "Error",
									JOptionPane.ERROR_MESSAGE);
							repaint();

						}
					}
				} catch (NumberFormatException ex) {
					// Si ocurre una excepción, mostrar el icono de alerta y resaltar el campo
					JOptionPane.showMessageDialog(null, "Error en los campos de texto.", "Error",
							JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		CotizarBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {

				CotizarBtn.setForeground(Color.WHITE);
				CotizarBtn.setBorderColor(new Color(1, 179, 169));
				CotizarBtn.setBackground(new Color(1, 179, 169));
			}
		});
		CotizarBtn.setText("AGREGAR A COTIZACION");
		CotizarBtn.setColorOver(new Color(119, 193, 255));
		CotizarBtn.setColorClick(new Color(15, 147, 255));
		CotizarBtn.setForeground(Color.WHITE);
		CotizarBtn.setBorderColor(new Color(1, 179, 169));
		CotizarBtn.setBackground(new Color(1, 179, 169));
		CotizarBtn.setBounds(422, 266, 178, 34);
		add(CotizarBtn);

		IngreseLaCantidad.setText("Ingrese la cantidad");
		IngreseLaCantidad.setForeground(Color.WHITE);
		IngreseLaCantidad.setFont(new Font("SansSerif", Font.BOLD, 14));
		IngreseLaCantidad.setBounds(28, 212, 201, 19);
		add(IngreseLaCantidad);

		Buscar.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				while (Buscar.getText().isEmpty())
					Buscar.setText("BUSCAR...");

			}
		});
		Buscar.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				Buscar.setText(null);
			}
		});

		Buscar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				filterComboBox(Buscar.getText());
			}
		});
		Buscar.setText("BUSCAR...");
		Buscar.setRound(0);

		Buscar.setBounds(262, 11, 87, 34);
		Buscar.setForeground(Color.WHITE);
		Buscar.setBackground(new Color(1, 179, 169));
		add(Buscar);
		PrecioTotalRepuestos.setEditable(false);

		PrecioTotalRepuestos.setText("USD " + precioTotalRepuestos.toString());
		PrecioTotalRepuestos.setRound(0);
		PrecioTotalRepuestos.setForeground(Color.WHITE);
		PrecioTotalRepuestos.setBounds(1045, 311, 126, 34);
		add(PrecioTotalRepuestos);

		Timer timer = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				formattedDate = dateFormat.format(currentDate);
				// Actualizar el JTextField con la fecha formateada
				Fecha.setText(formattedDate);
			}
		});

		// Iniciar el Timer
		timer.start();

		mbtnDarBaja.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				NombreCliente.setForeground(Color.WHITE);
				CondicionDePago.setForeground(Color.WHITE);
				ValidezCotizacion.setForeground(Color.WHITE);
				Atte.setForeground(Color.WHITE);
				Fecha.setForeground(Color.white);
				UIManager.put("Button.background", new Color(0, 188, 198));
				UIManager.put("Button.foreground", Color.WHITE);
				// Crea el mensaje con los componentes

				try {
					// Establece el Look and Feel de Windows
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, "Error UI.", "Error",
							JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}

				// Muestra el diálogo de entrada de datos
				int result = JOptionPane.showConfirmDialog(null, message, "Ingrese los datos",
						JOptionPane.OK_CANCEL_OPTION);

				// Si se selecciona "OK" en el diálogo
				if (result == JOptionPane.OK_OPTION) {
					// Verificar si algún campo está vacío
					if (NombreCliente.getText().isEmpty() || CondicionDePago.getText().isEmpty()
							|| ValidezCotizacion.getText().isEmpty() || Atte.getText().isEmpty()) {
						// Mostrar mensaje de error si algún campo está vacío
						JOptionPane.showMessageDialog(null, "Todos los campos deben ser llenados.", "Error",
								JOptionPane.ERROR_MESSAGE);
					} else {
						// Obtiene los datos ingresados

						data.setAtte(Atte.getText());
						data.setCondicionDePago(CondicionDePago.getText());
						data.setFecha(Fecha.getText());
						data.setNombreCliente(NombreCliente.getText());
						data.setValidezCotizacion(ValidezCotizacion.getText());
						data.setPrecioTotal(precioTotalRepuestos);
						data.setTipo("EXTERIOR");

						// Agrega la tabla al PDF
						PDGenerator.addTableToPDF(tableDark1, data);

						// Borra los repuestos cotizados

						for (RepuestoExterior RepuestoExterior : repuestosCotizados) {
							try {
								repcotdaoo.borrarRepuestoCotizado(RepuestoExterior.getIdrepuestoCotizado());
							} catch (SQLException e1) {
								JOptionPane.showMessageDialog(null, "Error al borrar.", "Error",
										JOptionPane.ERROR_MESSAGE);
								e1.printStackTrace();
							}
						}
					}
				}
			}
		});

		mbtnDarBaja.setText("CREAR PDF");
		mbtnDarBaja.setColorOver(new Color(119, 193, 255));
		mbtnDarBaja.setColorClick(new Color(15, 147, 255));
		mbtnDarBaja.setForeground(Color.WHITE);
		mbtnDarBaja.setBorderColor(new Color(1, 179, 169));
		mbtnDarBaja.setBackground(new Color(1, 179, 169));
		mbtnDarBaja.setBounds(636, 311, 106, 34);
		mbtnDarBaja.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				mbtnDarBaja.setForeground(Color.WHITE);
				mbtnDarBaja.setBorderColor(new Color(1, 179, 169));
				mbtnDarBaja.setBackground(new Color(1, 179, 169));
			}
		});

		add(mbtnDarBaja);

		lblPrecioTotalCotizacion.setText("Precio Total Cotizacion");
		lblPrecioTotalCotizacion.setForeground(Color.WHITE);
		lblPrecioTotalCotizacion.setFont(new Font("SansSerif", Font.PLAIN, 12));
		lblPrecioTotalCotizacion.setBounds(892, 311, 143, 19);
		add(lblPrecioTotalCotizacion);

		lblCotizacionExterior.setText("Cotizacion exterior");
		lblCotizacionExterior.setForeground(Color.WHITE);
		lblCotizacionExterior.setFont(new Font("SansSerif", Font.BOLD, 14));
		lblCotizacionExterior.setBounds(28, 11, 201, 19);
		add(lblCotizacionExterior);

		reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				try {
					repcotdaoo.borrarRepuestosExteriores();
					updateTable();
					JOptionPane.showMessageDialog(null, "Datos borrados correctamente");
				} catch (SQLException e1) {
					
					JOptionPane.showMessageDialog(null, "Error al borrar.", "Error",
							JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}
			}
		});
		reset.setText("RESETEAR");
		reset.setForeground(Color.WHITE);
		reset.setColorOver(new Color(119, 193, 255));
		reset.setColorClick(new Color(15, 147, 255));
		reset.setBorderColor(new Color(1, 179, 169));
		reset.setBackground(new Color(1, 179, 169));
		reset.setBounds(752, 311, 106, 34);
		add(reset);
		reset.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				reset.setForeground(Color.WHITE);
				reset.setBorderColor(new Color(1, 179, 169));
				reset.setBackground(new Color(1, 179, 169));
			}
		});

	}

	private ArrayList<String> listaCombobox() {
		ArrayList<String> ar = new ArrayList<>();
		for (Repuesto e : repuestos) {
			String value = e.getNumeroDeParte();
			ar.add(value);
		}
		return ar;
	}

	private List<String> filteredList = new ArrayList<>();

	private void filterComboBox(String filter) {
		for (String repuesto : listaCombobox()) {
			if (repuesto.toLowerCase().contains(filter.toLowerCase())) {
				filteredList.add(repuesto);
			}
		}
		comboBoxModel.removeAllElements();
		for (String repuesto : filteredList) {
			comboBoxModel.addElement(repuesto);
		}
		combobox.setPopupVisible(true);
	}

	private BigDecimal precioTotal() {

		try {
			cantidad = new BigDecimal(Cantidad.getText());
			if (!Precio.getText().isEmpty()) {
				precioRepuesto = new BigDecimal(Precio.getText());
			} else {
				// Si el campo Precio está vacío, devolvemos 0
				return BigDecimal.ZERO;
			}
		} catch (NumberFormatException e) {
		
			return BigDecimal.ZERO;
		}
		if (cantidad.compareTo(BigDecimal.ZERO) <= 0 || precioRepuesto.compareTo(BigDecimal.ZERO) <= 0) {
			// Si la cantidad o el precio son negativos o cero, devolvemos 0
			return BigDecimal.ZERO;
		}
		return cantidad.multiply(precioRepuesto);
	}

	private TableDark tableDark1 = new TableDark();

	private void updateTable() {

		// Reinicia el modelo de la tabla

		model.setRowCount(0);
		// Reinicia el precio total de los repuestos
		precioTotalRepuestos = BigDecimal.ZERO;
		// Consulta los repuestos cotizados
		repuestosCotizados = repcotdaoo.consultarTodosLosRepuestosExteriores();
		// Llena la tabla con los repuestos cotizados y suma los precios totales
		for (RepuestoExterior p : repuestosCotizados) {
			Object[] fila = new Object[model.getColumnCount()];
			fila[0] = p.getRepuesto().getNumeroDeParte();
			fila[1] = p.getRepuesto().getDescripcion();
			fila[2] = p.getPrecioExterior();
			fila[3] = p.getCantidad();
			fila[4] = p.getPrecioTotal();
			model.addRow(fila);
			precioTotalRepuestos = precioTotalRepuestos.add(p.getPrecioTotal()); // Suma al precio total
		}
		// Actualiza la tabla

	}

	private void updatePanel() {
		// Limpiar la tabla

		// Actualizar la tabla
		updateTable();
		tableDark1.validate();
		usdLabels();
		model.fireTableDataChanged();
		// Actualizar el total de los precios de los repuestos
		PrecioTotalRepuestos.setText("USD " + precioTotalRepuestos.toString());
	}

	private void usdLabels() {

		for (int row = 0; row < model.getRowCount(); row++) {
			for (int col = 0; col < model.getColumnCount(); col++) {
				String text = model.getValueAt(row, col).toString();
				if (col == 2 || col == 4) {

					text = "USD " + text;
					model.setValueAt(text, row, col);
				}
			}
		}
	}

	private void updatePriceTotal() {
		// Actualizar el campo de texto de precio total
		precioTotal = precioTotal();
		if (precioTotal.compareTo(BigDecimal.ZERO) > 0) {
			PrecioTotal.setText(precioTotal.toString());
		} else {
			PrecioTotal.setText("USD 0");
		}
	}
}
