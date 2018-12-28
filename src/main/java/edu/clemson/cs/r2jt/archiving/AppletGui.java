/*
 * AppletGui.java
 * ---------------------------------
 * Copyright (c) 2019
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.archiving;

public class AppletGui {

    private String appletName;
    private String facilityName;
    private String programFacilityName;
    private String packageName;
    private GuiSetup gui;
    private StringBuffer sb = new StringBuffer();

    public AppletGui(String an, String fn, String pfn, String pn, GuiSetup gui) {
        appletName = an;
        facilityName = fn;
        programFacilityName = pfn;
        packageName = pn;
        this.gui = gui;
    }

    public StringBuffer generateAppletCode() {
        sb.delete(0, sb.length());
        sb.append("package " + packageName + ";\n");
        sb.append("" + "\n");
        sb.append("import java.applet.Applet;" + "\n");
        sb.append("import java.awt.Button;" + "\n");
        sb.append("import java.awt.Label;" + "\n");
        sb.append("import java.awt.Rectangle;" + "\n");
        sb.append("import java.awt.TextArea;" + "\n");
        sb.append("import java.awt.TextField;" + "\n");
        sb.append("import java.awt.event.ActionEvent;" + "\n");
        sb.append("import java.awt.event.ActionListener;" + "\n");
        sb.append("import java.awt.event.KeyEvent;" + "\n");
        sb.append("import java.awt.event.KeyListener;" + "\n");
        sb.append("import java.io.IOException;" + "\n");
        sb.append("import java.io.InputStream;" + "\n");
        sb.append("import java.io.OutputStream;" + "\n");
        sb.append("import java.io.PipedInputStream;" + "\n");
        sb.append("import java.io.PipedOutputStream;" + "\n");
        sb.append("import java.io.PrintStream;" + "\n");
        sb.append("import java.text.SimpleDateFormat;" + "\n");
        sb.append("import java.util.Date;" + "\n");
        sb.append("import javax.swing.SwingUtilities;" + "\n");
        sb.append("" + "\n");
        sb.append("public class " + appletName
                + " extends Applet implements ActionListener, KeyListener{"
                + "\n");
        sb.append("    private final int width = " + gui.getWidth() + ";"
                + "\n");
        sb.append("    private final int height = " + gui.getHeight() + ";"
                + "\n");
        sb.append("" + "\n");
        sb
                .append("    private final Rectangle facilityLabelRec = new Rectangle("
                        + (int) gui.getFacilityLabelRec().getX()
                        + ","
                        + (int) gui.getFacilityLabelRec().getY()
                        + ","
                        + (int) gui.getFacilityLabelRec().getWidth()
                        + ","
                        + (int) gui.getFacilityLabelRec().getHeight()
                        + ");"
                        + "\n");
        sb.append("    private final Rectangle inputLabelRec = new Rectangle("
                + (int) gui.getInputLabelRec().getX() + ","
                + (int) gui.getInputLabelRec().getY() + ","
                + (int) gui.getInputLabelRec().getWidth() + ","
                + (int) gui.getInputLabelRec().getHeight() + ");" + "\n");
        sb.append("    private final Rectangle outputLabelRec = new Rectangle("
                + (int) gui.getOutputLabelRec().getX() + ","
                + (int) gui.getOutputLabelRec().getY() + ","
                + (int) gui.getOutputLabelRec().getWidth() + ","
                + (int) gui.getOutputLabelRec().getHeight() + ");" + "\n");
        sb.append("    private final Rectangle inputFieldRec = new Rectangle("
                + (int) gui.getInputFieldRec().getX() + ","
                + (int) gui.getInputFieldRec().getY() + ","
                + (int) gui.getInputFieldRec().getWidth() + ","
                + (int) gui.getInputFieldRec().getHeight() + ");" + "\n");
        sb
                .append("    private final Rectangle outputTextAreaRec = new Rectangle("
                        + (int) gui.getOutputTextAreaRec().getX()
                        + ","
                        + (int) gui.getOutputTextAreaRec().getY()
                        + ","
                        + (int) gui.getOutputTextAreaRec().getWidth()
                        + ","
                        + (int) gui.getOutputTextAreaRec().getHeight()
                        + ");"
                        + "\n");
        sb.append("    private final Rectangle runButtonRec = new Rectangle("
                + (int) gui.getRunButtonRec().getX() + ","
                + (int) gui.getRunButtonRec().getY() + ","
                + (int) gui.getRunButtonRec().getWidth() + ","
                + (int) gui.getRunButtonRec().getHeight() + ");" + "\n");
        /*sb.append("    private final Rectangle enterButtonRec = new Rectangle(" +
        				(int) gui.getEnterButtonRec().getX() + "," +
        				(int) gui.getEnterButtonRec().getY() + "," +
        				(int) gui.getEnterButtonRec().getWidth() + "," +
        				(int) gui.getEnterButtonRec().getHeight() + ");"+"\n");*/
        sb.append("    private final Rectangle genMessageRec = new Rectangle("
                + (int) gui.getGenMessageRec().getX() + ","
                + (int) gui.getGenMessageRec().getY() + ","
                + (int) gui.getGenMessageRec().getWidth() + ","
                + (int) gui.getGenMessageRec().getHeight() + ");" + "\n");
        sb.append("    private final Rectangle genDateRec = new Rectangle("
                + (int) gui.getGenDateRec().getX() + ","
                + (int) gui.getGenDateRec().getY() + ","
                + (int) gui.getGenDateRec().getWidth() + ","
                + (int) gui.getGenDateRec().getHeight() + ");" + "\n");
        sb.append("" + "\n");
        sb.append("    private final String programFacilityName = \""
                + programFacilityName + "\";" + "\n");
        sb.append("    private final String consoleInMsg = \""
                + gui.getConsoleInMsg() + "\";" + "\n");
        sb.append("    private final String consoleOutMsg = \""
                + gui.getConsoleOutMsg() + "\";" + "\n");
        sb.append("    private final String facilityLabelMsg = \""
                + gui.getFacilityLabelMsg() + ";" + "\n");
        sb.append("    private final String runButtonMsg = \""
                + gui.getRunButtonMsg() + "\";" + "\n");
        //sb.append("    private final String enterButtonMsg = \"" + gui.getEnterButtonMsg() + "\";"+"\n");
        sb.append("    private final String launchMsg = \""
                + gui.getLaunchMsg() + ";" + "\n");
        sb.append("    private final String completeMsg = \""
                + gui.getCompleteMsg() + ";" + "\n");
        sb.append("    private final String genMsg = \"" + gui.getGenMsg()
                + "\";" + "\n");
        sb.append("    private final Date date = new Date("
                + gui.getDate().getYear() + "," + gui.getDate().getMonth()
                + "," + gui.getDate().getDate() + ","
                + gui.getDate().getHours() + "," + gui.getDate().getMinutes()
                + "," + gui.getDate().getSeconds() + ");" + "\n");
        sb.append("    private final String dateFormatString = \""
                + gui.getDateFormatString() + "\";" + "\n");
        sb.append("" + "\n");
        sb.append("    private Label facilityLabel;" + "\n");
        sb.append("    private Label inputLabel;" + "\n");
        sb.append("    private Label outputLabel;" + "\n");
        sb.append("    private JLabel genMsgLabel;" + "\n");
        sb.append("    private JLabel dateLabel;" + "\n");
        sb.append("    private TextArea outputTextArea;" + "\n");
        sb.append("    private TextField inputField;" + "\n");
        sb.append("    private Button runButton;" + "\n");
        sb.append("    private Button enterButton;" + "\n");
        sb.append("    private Thread programThread;" + "\n");
        sb.append("    private PrintStream oldOut;" + "\n");
        sb.append("    private PrintStream oldErr;" + "\n");
        sb.append("    private static OutputStream out = null;" + "\n");
        sb.append("    private static PrintStream newOut = null;" + "\n");
        sb.append("    private static PipedOutputStream op = null;" + "\n");
        sb.append("    private static PipedInputStream ip = null;" + "\n");
        sb.append("" + "\n");
        sb.append("    private boolean programRunning = false;" + "\n");
        sb.append("" + "\n");
        sb.append("    @Override" + "\n");
        sb.append("    public void init() {" + "\n");
        sb.append("        setLayout(null);" + "\n");
        sb.append("        setSize(width, height);" + "\n");
        sb
                .append("        facilityLabel = new Label(facilityLabelMsg);"
                        + "\n");
        sb.append("        inputLabel = new Label(consoleInMsg);" + "\n");
        sb.append("        outputLabel = new Label(consoleOutMsg);" + "\n");
        sb.append("        inputField = new TextField(\"\",100);" + "\n");
        sb.append("        outputTextArea =  new TextArea(\"\", 40, 40);"
                + "\n");
        sb.append("        runButton = new JButton(runButtonMsg);" + "\n");
        //sb.append("        enterButton = new JButton(enterButtonMsg);"+"\n");
        sb.append("        facilityLabel.setBounds(facilityLabelRec);" + "\n");
        sb.append("        inputLabel.setBounds(inputLabelRec);" + "\n");
        sb.append("        outputLabel.setBounds(outputLabelRec);" + "\n");
        sb.append("        inputField.setBounds(inputFieldRec);" + "\n");
        sb
                .append("        outputTextArea.setBounds(outputTextAreaRec);"
                        + "\n");
        sb.append("        runButton.setBounds(runButtonRec);" + "\n");
        //sb.append("        enterButton.setBounds(enterButtonRec);"+"\n");
        //sb.append("        enterButton.setEnabled(false);"+"\n");
        sb.append("        add(facilityLabel);" + "\n");
        sb.append("        add(inputLabel);" + "\n");
        sb.append("        add(outputLabel);" + "\n");
        sb.append("        add(runButton);" + "\n");
        //sb.append("        add(enterButton);"+"\n");
        sb.append("        add(outputTextArea);" + "\n");
        sb.append("        add(inputField);" + "\n");
        //sb.append("        enterButton.addActionListener(this);"+"\n");
        sb.append("        runButton.addActionListener(this);" + "\n");
        sb.append("        initGenMsg()" + "\n");
        sb.append("    }" + "\n");
        sb.append("" + "\n");
        sb.append("    @Override" + "\n");
        sb.append("    public void start(){" + "\n");
        sb.append("        oldOut = System.out;" + "\n");
        sb.append("        oldErr = System.err;" + "\n");
        sb.append("        out = redirectSystemStreams();" + "\n");
        sb.append("        newOut = new PrintStream(out, true);" + "\n");
        sb.append("        System.setOut(newOut);" + "\n");
        sb.append("        System.setErr(newOut);" + "\n");
        sb.append("        if(ip == null){" + "\n");
        sb.append("            initPipes();" + "\n");
        sb.append("        }" + "\n");
        sb.append("    }" + "\n");
        sb.append("" + "\n");
        sb.append("    @Override" + "\n");
        sb.append("    public void stop(){" + "\n");
        sb.append("        System.setOut(oldOut);" + "\n");
        sb.append("        System.setErr(oldErr);" + "\n");
        sb.append("    }" + "\n");
        sb.append("" + "\n");
        sb.append("    @Override" + "\n");
        sb.append("    public void destroy(){" + "\n");
        sb.append("        this.removeAll();" + "\n");
        sb.append("        programThread = null;" + "\n");
        sb.append("    }" + "\n");
        sb.append("" + "\n");
        sb.append("" + "\n");
        sb.append("    private void initGenMsg(){" + "\n");
        sb.append("        genMsgLabel = new Label(genMsg);" + "\n");
        sb
                .append("        SimpleDateFormat sdf = new SimpleDateFormat(dateFormatString);"
                        + "\n");
        sb.append("        dateLabel = new Label(sdf.format(date));" + "\n");
        sb.append("        genMsgLabel.setBounds(genMessageRec);" + "\n");
        sb.append("        dateLabel.setBounds(genDateRec);" + "\n");
        sb.append("        add(genMsgLabel);" + "\n");
        sb.append("        add(dateLabel);" + "\n");
        sb.append("    }" + "\n");
        sb.append("" + "\n");
        sb.append("    public void keyPressed(KeyEvent e) {" + "\n");
        sb.append("        if(programRunning){" + "\n");
        sb
                .append("            if(e.getKeyCode() == KeyEvent.VK_ENTER){"
                        + "\n");
        sb.append("                writeText();" + "\n");
        sb.append("            }" + "\n");
        sb.append("        }" + "\n");
        sb.append("    }" + "\n");
        sb.append("" + "\n");
        sb.append("    public void keyTyped(KeyEvent e) {" + "\n");
        sb.append("    }" + "\n");
        sb.append("" + "\n");
        sb.append("    public void keyReleased(KeyEvent e) {" + "\n");
        sb.append("    }" + "\n");
        sb.append("" + "\n");
        sb.append("    public void actionPerformed(ActionEvent evt){" + "\n");
        //sb.append("        if(evt.getSource() == enterButton){"+"\n");
        //sb.append("            writeText();"+"\n");
        //sb.append("        }"+"\n");
        sb.append("        if(evt.getSource() == runButton){" + "\n");
        sb.append("            inputField.setText(\"\");" + "\n");
        sb.append("            programRunning = true;" + "\n");
        sb.append("            runButton.setEnabled(false);" + "\n");
        //sb.append("            enterButton.setEnabled(true);"+"\n");
        sb.append("            startThread();" + "\n");
        sb.append("        }" + "\n");
        sb.append("    }" + "\n");
        sb.append("" + "\n");
        sb.append("    private void writeText(){" + "\n");
        sb.append("        try {" + "\n");
        sb.append("            String text = inputField.getText() + \"\\n\";"
                + "\n");
        sb.append("            System.out.println(text);" + "\n");
        sb.append("            op.write(text.getBytes(\"UTF-8\"));" + "\n");
        sb.append("            op.flush();" + "\n");
        sb.append("        } catch (IOException ex) {" + "\n");
        sb.append("            System.out.println(ex);" + "\n");
        sb.append("        }" + "\n");
        sb.append("        inputField.setText(\"\");" + "\n");
        sb.append("    }" + "\n");
        sb.append("" + "\n");
        sb.append("    private void startThread(){" + "\n");
        sb.append("        Runnable r = new Runnable(){" + "\n");
        sb.append("            public void run(){" + "\n");
        sb.append("                    launchProgram(ip, newOut);" + "\n");
        sb.append("            }" + "\n");
        sb.append("        };" + "\n");
        sb.append("        programThread = new Thread(r);" + "\n");
        sb.append("        programThread.start();" + "\n");
        sb.append("    }" + "\n");
        sb.append("" + "\n");
        sb
                .append("    private void launchProgram(PipedInputStream ip, PrintStream newOut){"
                        + "\n");
        sb.append("        InputStream p = System.in;" + "\n");
        sb.append("        System.setIn(ip);" + "\n");
        sb.append("        System.setOut(newOut);" + "\n");
        sb.append("        System.setErr(newOut);" + "\n");
        sb.append("        System.out.println(launchMsg);" + "\n");
        sb.append("        " + facilityName + ".main(null);" + "\n");
        sb.append("        System.out.println(completeMsg);" + "\n");
        sb.append("        System.setIn(p);" + "\n");
        sb.append("        programRunning = false;" + "\n");
        sb.append("        runButton.setEnabled(true);" + "\n");
        //sb.append("        enterButton.setEnabled(false);"+"\n");
        sb.append("    }" + "\n");
        sb.append("" + "\n");
        sb.append("    private void updateOutputBuffer(final String text) {"
                + "\n");
        sb.append("        SwingUtilities.invokeLater(new Runnable() {" + "\n");
        sb.append("            public void run() {" + "\n");
        sb.append("                outputTextArea.append(text);" + "\n");
        sb.append("            }" + "\n");
        sb.append("        });" + "\n");
        sb.append("    }" + "\n");
        sb.append("" + "\n");
        sb.append("    private OutputStream redirectSystemStreams() {" + "\n");
        sb.append("        OutputStream os = new OutputStream() {" + "\n");
        sb.append("            @Override" + "\n");
        sb.append("            public void write(int b) throws IOException {"
                + "\n");
        sb
                .append("                updateOutputBuffer(String.valueOf((char) b));"
                        + "\n");
        sb.append("            }" + "\n");
        sb.append("" + "\n");
        sb.append("            @Override" + "\n");
        sb
                .append("            public void write(byte[] b, int off, int len) throws IOException {"
                        + "\n");
        sb
                .append("                updateOutputBuffer(new String(b, off, len));"
                        + "\n");
        sb.append("            }" + "\n");
        sb.append("" + "\n");
        sb.append("            @Override" + "\n");
        sb
                .append("            public void write(byte[] b) throws IOException {"
                        + "\n");
        sb.append("                write(b, 0, b.length);" + "\n");
        sb.append("            }" + "\n");
        sb.append("        };" + "\n");
        sb.append("        return os;" + "\n");
        sb.append("    }" + "\n");
        sb.append("" + "\n");
        sb.append("    private void initPipes(){" + "\n");
        sb.append("        ip = new PipedInputStream();" + "\n");
        sb.append("        try {" + "\n");
        sb.append("            op = new PipedOutputStream(ip);" + "\n");
        sb.append("        } catch (IOException ex) {" + "\n");
        sb.append("            System.out.println(ex);" + "\n");
        sb.append("        }" + "\n");
        sb.append("    }" + "\n");
        sb.append("}" + "\n");
        return sb;
    }

}
