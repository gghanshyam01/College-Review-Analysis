/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import weka.classifiers.functions.SMO;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.StringToWordVector;

/**
 *
 * @author GHANSHYAM
 */
@WebServlet(name = "ReviewPredictor", urlPatterns = {"/ReviewPredictor"})
public class ReviewPredictor extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    String comment = "";
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            // No considerable reason to have 2-if conds but this may increase performance i believe ;)
            if (request.getParameter("sentiment") != null) { // Coming from js function prediction() 
                addComment(request, response, request.getParameter("sentiment"));
            } else if(request.getParameter("feedback") != null) {
                try {
                    addFeedback(request, response);
                } catch (Exception ex) {
                    String msg = "<div class=\"alert alert-danger role=\"alert\">\n" +
                                    "<strong>Error receiving feedback !</strong>." + ex.toString()+ "\n" +
                                    "</div>";
                    request.setAttribute("response", msg);
                    request.getRequestDispatcher("Contact.jsp").forward(request, response);
                }
            }
            else if (request.getParameter("review") != null){ // Coming from js function openModal().
                comment = request.getParameter("review");
                try {
                    
                    //out.print("Analyzing review...");
                    // Instances class is used to select a dataset. The constructor takes the file path as parameter.
                    BufferedReader br = new BufferedReader(new FileReader("E:\\Practicals\\MCA\\Java\\CollegeReview\\src\\arff file\\reviews Segregated.arff"));
                    Instances trainingData = new Instances(br);
                    trainingData.setClassIndex(trainingData.numAttributes() - 1);
                    br.close();
                    ArrayList<String> classVal = new ArrayList<>(2);
                    classVal.add("neg");
                    classVal.add("pos");

                    Attribute attr1 = new Attribute(trainingData.attribute(0).name(), (ArrayList<String>)null);
                    Attribute attr2 = new Attribute(trainingData.attribute(1).name(), classVal);

                    ArrayList<Attribute> attributes = new ArrayList<>(trainingData.numAttributes());
                    attributes.add(attr1);
                    attributes.add(attr2);

                    Instances inst = new Instances("Test", attributes, 1);
                    inst.setClassIndex(inst.numAttributes() - 1);

                    DenseInstance dInst = new DenseInstance(inst.numAttributes());
                    dInst.setValue(attr1, comment);

                    inst.add(dInst);

                    // System.out.println(trainingData);
                    // setClassIndex() is used to refer the column no. which has the answer.

                    StringToWordVector stwv = new StringToWordVector();
                    stwv.setInputFormat(trainingData);
                    stwv.setIDFTransform(true);
                    stwv.setTFTransform(true);
                    stwv.setLowerCaseTokens(true);
                    stwv.setOutputWordCounts(true);

                    // Creating a classifier, in my case, im using Support Vector Machines.
                    FilteredClassifier fc = new FilteredClassifier();
                    fc.setFilter(stwv);
                    fc.setClassifier(new SMO());
                    fc.buildClassifier(trainingData);

                    // Now im building that classifier from my training dataset.


                    // My 'testdata' has user-entered values(see above)).
                    // Im passing it to the classifier to get prediction.
                    double prediction = fc.classifyInstance(inst.instance(0));

                    out.print(inst.classAttribute().value((int)prediction));
                    // Here Im just displaying my predictions using a Dialog box.
                    //JOptionPane.showMessageDialog(this, inst.classAttribute().value((int)prediction), "Result", JOptionPane.INFORMATION_MESSAGE);
                } catch(Exception ex) {
                    out.print("Error Occurred: " + ex.toString());
                    //JOptionPane.showMessageDialog(this, ex.getMessage(), ex.getClass().toString(), JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    protected void addComment(HttpServletRequest request, HttpServletResponse response, String sentiment)
            throws ServletException, IOException {
        System.out.println("Sentiment: " + sentiment);
        // TODO add comment into DB
    }
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private void addFeedback(HttpServletRequest request, HttpServletResponse response)
    throws Exception {
        PrintWriter out = response.getWriter();
        List<String> values = new ArrayList<> (3);
        String username = LoginCheck.session.getAttribute("user").toString();
        if (username == null) throw new Exception("Session Expired, Login again to continue.");
        values.add(username);
        values.add(request.getParameter("email"));
        values.add(request.getParameter("feedback"));
        String query = "insert into feedbackdetails values (?, ?, ?)";
        if (DbConnect.insertViaPreparedStatement(query, values, false) == 1) {
            DbConnect.closeConnection(null);
            String msg = "<div class=\"alert alert-success role=\"alert\">\n" +
                                    "<strong>Feedback Received !</strong> We will get back to you shortly.\n" +
                                    "</div>";
            request.setAttribute("response", msg);
        } else {
            DbConnect.closeConnection(null);
            String msg = "<div class=\"alert alert-danger role=\"alert\">\n" +
                                    "<strong>Error receiving feedback !</strong> Please try again or contact administrator.\n" +
                                    "</div>";
            request.setAttribute("response", msg);        
        }
        request.getRequestDispatcher("Contact.jsp").forward(request, response);
    }
}
