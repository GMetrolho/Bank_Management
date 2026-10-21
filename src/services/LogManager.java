package services;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * Classe utilitária estática responsável por gerir e registar eventos do sistema
 * num ficheiro de log em formato XML.
 * 
 * O ficheiro de log é NOME_FICHEIRO, e os novos eventos são anexados
 * ao elemento raiz <logs>.
 * 
 */
public class LogManager {

	/** Nome do ficheiro XML onde os logs serão escritos. */
    private static final String NOME_FICHEIRO = "monitor_contas_log.xml";
    /** Formatador para garantir um padrão de data/hora consistente no log. */
    private static final DateTimeFormatter FORMATO_DATA = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    
    
    /**
     * Escreve o objeto Document XML atual para o ficheiro NOME_FICHEIRO.
     * Este método é responsável por configurar o Transformer para formatar o XML
     * (indentação).
     *
     * @param doc - O documento XML (org.w3c.dom.Document) a ser escrito no disco.
     */
    private static void writeXmlFile(Document doc) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(NOME_FICHEIRO));
            
            transformer.transform(source, result);
        } catch (Exception e) {
            System.err.println("Erro ao escrever o ficheiro XML: " + e.getMessage());
        }
    }
    
    
    /**
     * Carrega o documento XML existente do ficheiro de log ou cria um novo
     * documento com o elemento raiz <logs> se o ficheiro não existir.
     *
     * @return O objeto Document pronto para adicionar novos nós, ou {@code null} em caso de erro.
     */
    private static Document getLogDocument() {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            File logFile = new File(NOME_FICHEIRO);

            if (logFile.exists() && logFile.length() > 0) {
                return dBuilder.parse(logFile);
            } else {
                Document doc = dBuilder.newDocument();
                Element rootElement = doc.createElement("logs");
                doc.appendChild(rootElement);
                return doc;
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar ou criar Documento XML: " + e.getMessage());
            return null;
        }
    }

    
    /**
     * Regista um evento de aplicação de juros no ficheiro de log XML.
     * O método é synchronized para garantir que apenas um evento
     * seja escrito por vez, evitando corrupção do ficheiro em ambiente multi-threaded.
     *
     * @param idConta - O ID da conta onde os juros foram aplicados.
     * @param saldoAntigo - O saldo da conta antes da aplicação dos juros.
     * @param saldoNovo - O saldo da conta após a aplicação dos juros.
     */
    public static synchronized void logAplicacaoJuros(int idConta, double saldoAntigo, double saldoNovo) {
        
        Document doc = getLogDocument();
        if (doc == null) return;
        
        double jurosAplicados = saldoNovo - saldoAntigo;

        Element evento = doc.createElement("evento");
        
        Node logsRoot = doc.getDocumentElement();
        
        logsRoot.appendChild(evento);

        String sTimestamp = LocalDateTime.now().format(FORMATO_DATA);
        
       
        appendElement(doc, evento, "tipo", "APLICACAO_JUROS");
        appendElement(doc, evento, "id_conta", String.valueOf(idConta));
        // Uso de Locale.US para garantir o separador decimal '.' (ponto) no XML
        appendElement(doc, evento, "saldo_antigo", String.format(Locale.US, "%.2f", saldoAntigo));     
        appendElement(doc, evento, "juros_aplicados", String.format(Locale.US, "%.2f", jurosAplicados));
        appendElement(doc, evento, "saldo_novo", String.format(Locale.US, "%.2f", saldoNovo));
        appendElement(doc, evento, "timestamp", sTimestamp);

        writeXmlFile(doc);
        
        
    }
    
    
    /**
     * Método utilitário privado para criar um novo elemento, definir o seu conteúdo
     * de texto e anexá-lo a um elemento pai.
     *
     * @param doc - O documento XML.
     * @param parent - O elemento pai ao qual o novo elemento será anexado.
     * @param tagName - O nome do novo elemento (tag).
     * @param textContent - O conteúdo de texto do novo elemento.
     */
    private static void appendElement(Document doc, Element parent, String tagName, String textContent) {
        Element element = doc.createElement(tagName);
        element.setTextContent(textContent);
        parent.appendChild(element);
    }
}