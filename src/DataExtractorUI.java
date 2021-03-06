import org.apache.pdfbox.exceptions.COSVisitorException;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;


public class DataExtractorUI {
    private JButton browseButton;
    private JPanel jpanel;
    private JLabel picLabel;

    static JFrame newOne;
    static GraphData graphData;
    final DataExtractor dataExtractor = new DataExtractor();
    PDFTableGenerator pdfTableGenerator = new PDFTableGenerator();
    ImageGrid imageGrid;

    public DataExtractorUI() {
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Pdf files", "pdf");
                fileChooser.setAcceptAllFileFilterUsed(false);
                fileChooser.setFileFilter(filter);
                int returnValue = fileChooser.showOpenDialog(null);

                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    final File selectedFile = fileChooser.getSelectedFile();

                    File f = new File(selectedFile.getPath());
                    if(!f.exists())
                    {
                        JOptionPane.showMessageDialog(new JFrame(),"Select Existing file");
                        return;
                    }
                    System.out.println(selectedFile.getName());


                    //dataExtractor.extractDataTemp("./resources/try2.png");
                    final ArrayList<String> imageFileList = dataExtractor.getGraphImages(selectedFile.getPath());

                    imageGrid = new ImageGrid(imageFileList);
                    newOne = imageGrid.createAndShowGui(imageFileList, null);
                    final JFrame jFrame = new JFrame();
                    final JPanel bigPanel = new JPanel();
                    JPanel buttonPanel = new JPanel();
                    final JButton nextButton = new JButton("Next");

                    buttonPanel.add(nextButton);
                    bigPanel.setLayout(new BoxLayout(bigPanel, BoxLayout.PAGE_AXIS));
                    bigPanel.add(buttonPanel);
                    jFrame.setContentPane(bigPanel);

                    jFrame.pack();
                    jFrame.setVisible(true);
                    final Iterator iterator = imageFileList.iterator();

                    String imageFile = iterator.next().toString();
                     graphData = dataExtractor.extractDataForImage(imageFile);

                    final ImagePanel[] imagePanel = {new ImagePanel("./resources" + imageFile, graphData)};

                    jFrame.add(imagePanel[0].container);

                    jFrame.pack();

                    nextButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String xScale,yScale,caption,minX,maxX,minY,maxY;
                            minX = imagePanel[0].getTextFieldMinX().getText();
                            maxX = imagePanel[0].getTextFieldMaxX().getText();
                            minY = imagePanel[0].getTextFieldMinY().getText();
                            maxY = imagePanel[0].getTextFieldMaxY().getText();
                            if(!checkGoodValues(minX,maxX,minY,maxY))
                            {
                                //JOptionPane.showMessageDialog(new JFrame(), "give double values for min-max values");

                                JOptionPane.showMessageDialog(new JFrame(),"Check values entered");

                            }
                            else {
                                ArrayList<Double> minmaxValues = new ArrayList<Double>();
                                minmaxValues.add(Double.parseDouble(minX));
                                minmaxValues.add(Double.parseDouble(maxX));
                                minmaxValues.add(Double.parseDouble(minY));
                                minmaxValues.add(Double.parseDouble(maxY));
                                Double rValue = (Double.parseDouble(maxX)-Double.parseDouble(minX))/5.0;
                                minmaxValues.add(rValue);
                                GraphData newGraphData = imagePanel[0].getGraphData();
                                newGraphData.minmaxValues = minmaxValues;
                                dataExtractor.getPlotsAndLegend(newGraphData);

                                if (iterator.hasNext()) {
                                    System.out.println(imagePanel[0].container.getComponent(1));
                                    String imageFile = iterator.next().toString();
                                    graphData = dataExtractor.extractDataForImage(imageFile);
                                    jFrame.remove(imagePanel[0].container);
                                    imagePanel[0] = new ImagePanel("./resources" + imageFile, graphData);
                                    jFrame.setContentPane(bigPanel);
                                    jFrame.add(imagePanel[0].container);
                                    jFrame.pack();
}
                                else {

                                    try {
                                        pdfTableGenerator.generatePDF(dataExtractor.tableList,dataExtractor.captionList, selectedFile.getPath(),imageFileList);
                                    } catch (COSVisitorException e1) {
                                        e1.printStackTrace();
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }

                                    jFrame.dispose();
                                }
                            }

                        }
                    });

                }
            }
        });
    }

    private boolean checkGoodValues(String minX, String maxX, String minY, String maxY) {
        if(!isDouble(minX) || !isDouble(maxX) ||!isDouble(minY) || !isDouble(maxY)) return false;
        else {
            if(Double.parseDouble(minX)>Double.parseDouble(maxX)) return false;
            if(Double.parseDouble(minY)>Double.parseDouble(maxY)) return false;
            else return true;
        }
    }
    private static boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        JFrame frame = new JFrame("Graphito");
        frame.setContentPane(new DataExtractorUI().jpanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
