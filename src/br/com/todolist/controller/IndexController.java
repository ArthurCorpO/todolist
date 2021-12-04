package br.com.todolist.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import br.com.todolist.io.TarefaIO;
import br.com.todolist.model.StatusTarefa;
import br.com.todolist.model.Tarefa;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class IndexController implements Initializable, ChangeListener<Tarefa> {

	@FXML
	private DatePicker inpData;

	@FXML
	private TextField inpDescricao;

	@FXML
	private TextField inptStatus;

	@FXML
	private TextArea inpComent;

	@FXML
	private Button btnConc;

	@FXML
	private Button btnCalendar;

	@FXML
	private Button btnDelete;

	@FXML
	private Button btnSave;

	@FXML
	private Button btnRubber;

	@FXML
	private TableColumn<Tarefa, LocalDate> tcData;

	@FXML
	private TableColumn<Tarefa, String> tcTarefa;

	@FXML
	private TableView<Tarefa> tvTarefa;

	@FXML
	private Label lbConcluida;

	@FXML
	private TextField inpCodigo;

	private List<Tarefa> tarefas;

	private Tarefa tarefa;

	@FXML
	void clickCalendar(ActionEvent event) {
		if (tarefa != null) {
			int dias = Integer.parseInt(JOptionPane.showInputDialog(null, "Quantos dias voc� deseja adiar?",
					"Informe quantos dias", JOptionPane.QUESTION_MESSAGE));

			DateTimeFormatter padraoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");

			LocalDate novaData = tarefa.getDataLimite().plusDays(dias);
			tarefa.setDataLimite(novaData);
			tarefa.setStatus(StatusTarefa.ADIADA);

			try {
				TarefaIO.saveTarefas(tarefas);
				JOptionPane.showMessageDialog(null, "A Nova data � " + novaData.format(padraoData), "Adiado",
						JOptionPane.INFORMATION_MESSAGE);

				carregarTarefas();
				limpar();
			} catch (IOException e) {
				JOptionPane.showConfirmDialog(null, "Erro ao Salvar Tarefa", "ERROR", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		}
	}

	@FXML
	void clickConc(ActionEvent event) {
		if (tarefa != null) {
			tarefa.setStatus(StatusTarefa.CONCLUIDA);
			tarefa.setDataConcluida(LocalDate.now());
			try {
				JOptionPane.showMessageDialog(null, "Parab�ns, Tarefa Concluida!!!", "Joia",
						JOptionPane.INFORMATION_MESSAGE);

				TarefaIO.saveTarefas(tarefas);
				carregarTarefas();
				limpar();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Erro ao Concluir Tarefa", "ERROR", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		}
	}

	@FXML
	void clickDelete(ActionEvent event) {
		if (tarefa != null) {
			int opt = JOptionPane.showConfirmDialog(null, "Voc� deseja excluir a Tarefa " + tarefa.getId() + " ?",
					"Excluir", JOptionPane.YES_NO_OPTION);
			if (opt == 0) {
				tarefas.remove(tarefa);

				try {
					TarefaIO.saveTarefas(tarefas);
					carregarTarefas();
					limpar();
				} catch (IOException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "Ocorreu um erro ao Excluir", "Excluir",
							JOptionPane.INFORMATION_MESSAGE);
				}
			} else {
				return;
			}
		}
	}

	@FXML
	void clickRubber(ActionEvent event) {
		limpar();

	}

	@FXML
	void clickSave(ActionEvent event) {
		// Verificando se Valores est�o vazios
		if (inpData.getValue().isBefore(LocalDate.now())) {

			inpData.setStyle("-fx-border-color: red;");
			inpDescricao.setStyle("-fx-border-color: transparent;");
			inpComent.setStyle("-fx-border-color: transparent;");

			JOptionPane.showMessageDialog(null, "Informe uma data V�lida", "Alerta", JOptionPane.ERROR_MESSAGE);
		} else if (inpData.getValue() == null) {

			inpData.setStyle("-fx-border-color: red;");
			inpDescricao.setStyle("-fx-border-color: transparent;");
			inpComent.setStyle("-fx-border-color: transparent;");

			JOptionPane.showMessageDialog(null, "Informe a data de realiza��o", "Alerta", JOptionPane.ERROR_MESSAGE);
			inpData.requestFocus();

		} else if (inpDescricao.getText().isEmpty()) {

			inpData.setStyle("-fx-border-color: transparent;");
			inpDescricao.setStyle("-fx-border-color: red;");
			inpComent.setStyle("-fx-border-color: transparent;");

			JOptionPane.showMessageDialog(null, "Informe o Titulo", "Alerta", JOptionPane.ERROR_MESSAGE);
			inpDescricao.requestFocus();

		} else if (inpComent.getText().isEmpty()) {

			inpData.setStyle("-fx-border-color: transparent;");
			inpDescricao.setStyle("-fx-border-color: transparent;");
			inpComent.setStyle("-fx-border-color: red;");

			JOptionPane.showMessageDialog(null, "Informe a Descri��o", "Alerta", JOptionPane.ERROR_MESSAGE);
			inpComent.requestFocus();
		} else {
			// verifica se a tarefa e nula
			if (tarefa == null) {
				// instanciando tarefa
				tarefa = new Tarefa();
				tarefa.setDataCriacao(LocalDate.now());
				tarefa.setStatus(StatusTarefa.ABERTA);
			}

			tarefa.setDataLimite(inpData.getValue());
			tarefa.setDescricao(inpComent.getText());
			tarefa.setComentarios(inpDescricao.getText());

			try {
				if (tarefa.getId() == 0) {
					TarefaIO.insert(tarefa);
				} else {
					TarefaIO.saveTarefas(tarefas);
				}

				limpar();
				carregarTarefas();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Erro ao gravar: " + e.getMessage(), "Erro",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	// Limpar os campos
	private void limpar() {
		tarefa = null;
		inpData.setValue(null);
		inpComent.setText(null);
		inpDescricao.setText(null);
		inptStatus.setText(null);
		inpData.requestFocus();

		btnCalendar.setDisable(true);
		btnConc.setDisable(true);
		btnDelete.setDisable(true);
		inpData.setDisable(false);
		inpCodigo.setText(null);
		btnSave.setDisable(false);
		tvTarefa.getSelectionModel().clearSelection();

		inpData.setEditable(true);
		inpDescricao.setEditable(true);
		inpComent.setEditable(true);

		leitorID();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// definir os parametros para as colunas do TableView !n�o entendi nada
		tcData.setCellValueFactory(new PropertyValueFactory<>("dataLimite"));
		tcTarefa.setCellValueFactory(new PropertyValueFactory<>("descricao"));

		tcData.setCellFactory(call -> {
			return new TableCell<Tarefa, LocalDate>() {
				@Override
				protected void updateItem(LocalDate item, boolean empty) {
					DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

					if (!empty) {
						setText(item.format(fmt));
					} else {
						setText("");
					}

					super.updateItem(item, empty);
				}
			};

		});

		tvTarefa.setRowFactory(call -> new TableRow<Tarefa>() {
			protected void updateItem(Tarefa item, boolean empty) {
				super.updateItem(item, empty);

				if (item == null) {
					setStyle("");
				} else if (item.getStatus() == StatusTarefa.CONCLUIDA) {
					setStyle("-fx-background-color: MediumSeaGreen");
				} else if (item.getDataLimite().isBefore(LocalDate.now())) {
					setStyle("-fx-background-color: Maroon");
				} else if (item.getStatus() == StatusTarefa.ADIADA) {
					setStyle("-fx-background-color: Gold");
				} else {
					setStyle("-fx-background-color: CornflowerBlue");
				}
			};
		});

		tvTarefa.getSelectionModel().selectedItemProperty().addListener(this);

		carregarTarefas();

		leitorID();
	}

	public void carregarTarefas() {
		try {
			tarefas = TarefaIO.read();
			tvTarefa.setItems(FXCollections.observableArrayList(tarefas));
			tvTarefa.refresh();

		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Erro ao carregar as tarefas: " + e.getMessage(), "Erro",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	@Override
	public void changed(ObservableValue<? extends Tarefa> observable, Tarefa oldValue, Tarefa newValue) {
		// passo referencia para a variavel global
		tarefa = newValue;

		if (tarefa != null) {
			inpCodigo.setText(tarefa.getId() + "");
			inpData.setValue(tarefa.getDataLimite());
			inpDescricao.setText(tarefa.getDescricao());
			inpComent.setText(tarefa.getComentarios());
			inptStatus.setText(tarefa.getStatus() + "");

			btnDelete.setDisable(false);
			btnConc.setDisable(false);

			switch (tarefa.getStatus()) {
			case ADIADA:
				lbConcluida.setText("Data para realiza��o:");
				btnCalendar.setDisable(true);
				btnSave.setDisable(false);
				break;
			case CONCLUIDA:
				lbConcluida.setText("Data de Conclus�o:");
				inpData.setValue(tarefa.getDataConcluida());

				btnCalendar.setDisable(true);
				btnConc.setDisable(true);
				btnSave.setDisable(true);

				inpData.setEditable(false);
				inpDescricao.setEditable(false);
				inpComent.setEditable(false);
				break;
			default:
				lbConcluida.setText("Data para realiza��o:");
				btnSave.setDisable(false);
				btnCalendar.setDisable(false);
				inpData.setDisable(true);
				break;
			}

		}
	}

	public void leitorID() {
		try {
			inpCodigo.setText(TarefaIO.leId() + "");
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "Erro n�o foi possivel ler o ID", "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
}