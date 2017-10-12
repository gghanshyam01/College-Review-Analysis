/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.html.HTML;

/**
 *
 * @author GHANSHYAM
 */
@WebServlet(name = "LoginCheck", urlPatterns = {"/LoginCheck"})
public class LoginCheck extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    
    protected void serviceRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            if (request.getParameter("email") != null) {
                if (request.getParameter("password").equals(request.getParameter("password_conf"))) {
                    if (registerUser(request, response)) {
                        String msg = "<div class=\"alert alert-success\" role=\"alert\">\n" + "<strong>Registration Successful !</strong>" +
                                        "  <a href=\"Login.jsp\" class=\"alert-link\">Click here</a> to login.\n" +
                                        "</div>";
                        request.setAttribute("status", msg);
                        request.getRequestDispatcher("register.jsp").forward(request, response);
                    } else {
                        
                        String msg = "<div class=\"alert alert-warning role=\"alert\">\n" +
                                "<strong>Registration Failed !</strong> Please try again.\n" +
                                "</div>";
                        request.setAttribute("status", msg);
                        request.getRequestDispatcher("register.jsp").forward(request, response);
                    }
                } else {
                    String msg = "<div class=\"alert alert-danger role=\"alert\">\n" +
                        "<strong>Input Error !</strong> Password doesn\'t match.\n" +
                        "</div>";
                        request.setAttribute("status", msg);
                        request.getRequestDispatcher("register.jsp").forward(request, response);
                }
            } else if (request.getParameter("txtuser") != null){
                try {
                    String _uname = request.getParameter("txtuser");
                    String _pass = request.getParameter("txtpass");
                    System.out.println(_pass + " " + _uname);
                    if (_uname.equals("user") && _pass.equals("password")) {
                        if (request.getAttribute("h5") != null) request.removeAttribute("h5");
                        response.sendRedirect("HomePage.jsp");
                    } else {
                        request.setAttribute("h5", "<h6 style=\"color:red;\">Invalid username or password.</h6>");
                        request.getRequestDispatcher("Login.jsp").forward(request, response);
                        //out.println("Invalid username or password...");
                    }
                } catch (Exception ex) {
                    out.println("Exception: " + ex.getMessage());
                }
            }
        }
    }
    
    protected boolean registerUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<String> userDetails = new ArrayList<>(3);
            userDetails.add(request.getParameter("username"));
            userDetails.add(request.getParameter("email"));
            userDetails.add(request.getParameter("password"));
            // userDetails.add(request.getParameter("password_conf"));
            return (DbConnect.insertQuery(userDetails));
        }
        catch (Exception ex){
            throw ex;
        }
        
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        serviceRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        serviceRequest(request, response);
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

}
