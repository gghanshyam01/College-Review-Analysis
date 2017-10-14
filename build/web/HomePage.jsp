<%-- 
    Document   : HomePage
    Created on : Sep 16, 2017, 2:41:48 PM
    Author     : GHANSHYAM
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Home Page</title>
         <!-- Nice Scroll Js CDN -->
        <script src="https://use.fontawesome.com/2a1f89f3bf.js"></script>
        <script src="js/jquery.nicescroll.min.js"></script>
        <script src="js/jquery-3.2.1.min.js"></script>
        <!-- Bootstrap Js CDN -->
        <script src="js/bootstrap.min.js"></script>
        <script type="text/javascript">
            function getData(vl) {
                var xmlhttp=new XMLHttpRequest();
                xmlhttp.onreadystatechange=function()
                {
                    if(this.readyState === 4 && this.status === 200) {
                            document.getElementById("val").innerHTML=this.responseText;
                    } else {
                            //document.getElementById("val").innerHTML = "LOADING...";
                    }
                };
                xmlhttp.open("POST","http://localhost:8080/CollegeReview/DBNegotiator?cName="+vl + "&t=" + Math.random(),true);
                xmlhttp.send();
            }
            // When the user clicks the button, open the modal
            
            var modal;
            var senti;
            function openModal() {
                var modal = document.getElementById('myModal');
                console.log("In openModal()");
                document.getElementById('sentiment').innerHTML = 'Analyzing Review...';
                $('#yes').prop('disabled', true);
                $('#no').prop('disabled', true);

                if (document.getElementById('comment').value.length <= 1){
                    document.getElementById('cmt').blur();
                    return;
                }
                modal.style.display = "block";
                var xmlhttp=new XMLHttpRequest();
                var comment = document.getElementById('comment').value;
                xmlhttp.onreadystatechange=function()
                {
                    if(this.readyState === 4 && this.status === 200) {
                            senti = this.responseText;
                            document.getElementById("sentiment").innerHTML="<b><i>" + senti + "</i></b>";
                            $('#yes').prop('disabled', false);
                            $('#no').prop('disabled', false);
                            // document.getElementById("yes").disabled = false;
                            // document.getElementById("no").disabled = false;
                    } else {
                            //document.getElementById("val").innerHTML = "LOADING...";
                    }
                };
                xmlhttp.open("POST","http://localhost:8080/CollegeReview/ReviewPredictor?review="+comment+"&time="+Math.random(), true);
                xmlhttp.send();
                
            }
            
            $(document).ready(function() {
                
            // Get the modal
            modal = document.getElementById('myModal');

            // Get the button that opens the modal
            var btn = document.getElementById("cmt");

            // Get the <span> element that closes the modal
            var span = document.getElementsByClassName("close")[0];

            // When the user clicks on <span> (x), close the modal
            span.onclick = function() {
                modal.style.display = "none";
            };

            // When the user clicks anywhere outside of the modal, close it
            window.onclick = function(event) {
                if (event.target === modal) {
                    modal.style.display = "none";
                }
            };
            });
            function prediction(isCorrect) {
                modal.style.display = "none";
                var xmlhttp=new XMLHttpRequest();
                var url;
                console.log(isCorrect);
                if (isCorrect) {
                    url = "http://localhost:8080/CollegeReview/ReviewPredictor?sentiment=" + senti;
                    
                } else {
                    if (senti === "pos") {
                        url = "http://localhost:8080/CollegeReview/ReviewPredictor?sentiment=neg";
                    } else {
                        url = "http://localhost:8080/CollegeReview/ReviewPredictor?sentiment=pos";
                    }
                }
                xmlhttp.open("POST", url, true);
                xmlhttp.send();
                addComment(document.getElementById('comment'));
            }
            
            </script>
          <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>  
            <script>
            var commentNo = 0;
            function addComment(commentBox) {
                //$("#" + (commentNo + 1)).addClass('hide');
                if (commentBox.value.length > 1) {
                    var newComment = "<span id='" + ++commentNo + "'>" + commentBox.value + "</span><div class='line'></div>";
                    $(newComment).hide().appendTo('#creviews').fadeIn("slow");
                    //document.getElementById('creviews').innerHTML += 
                    commentBox.value = '';
                }
                document.getElementById('cmt').blur();
            }
                
            function getCollegeDetails(name) {
                console.log("In get()");
                var xmlhttp=new XMLHttpRequest();
                $('.tp').removeClass('hide');
                //xmlhttp.overrideMimeType('text/xml');
                xmlhttp.onreadystatechange = function()
                {
                    if(this.readyState === 4 && this.status === 200) {
                            data = this.responseXML;
                            var neg = data.getElementsByTagName("neg")[0].childNodes[0].nodeValue;
                            var pos = data.getElementsByTagName("pos")[0].childNodes[0].nodeValue;
                            cName = data.getElementsByTagName("name")[0].childNodes[0].nodeValue;
                            cSAddr = data.getElementsByTagName("saddr")[0].childNodes[0].nodeValue;
                            cAddr = data.getElementsByTagName("addr")[0].childNodes[0].nodeValue;
                            cWeb = data.getElementsByTagName("web")[0].childNodes[0].nodeValue;
                            cContact = data.getElementsByTagName("contact")[0].childNodes[0].nodeValue;
                            document.getElementById("cname").innerHTML = cName;
                            document.getElementById("saddr").innerHTML = cSAddr;
                            document.getElementById("caddr").innerHTML = "<b>Location:</b><br /><br />" + "<b>" + cName + "</b>" + "<br />" + cAddr;
                            courses = data.getElementsByTagName("course");
                            course = "<b>Courses Offered:</b><br /><br />" + "<ul>";
                            for (i = 0; i < courses.length; i++) {
                                course += "<li>" + courses[i].childNodes[0].nodeValue + "</li>";
                            }
                            course += "</ul>";
                            reviews = data.getElementsByTagName("review");
                            review = "<span><b>Student Reviews:</b><br /><br /></span>";
                            for (i = 0; i < reviews.length; i++) {
                                review += "<span id='" + ++commentNo + "'>" + reviews[i].childNodes[0].nodeValue + "</span><div class='line'></div>";
                            }
                            //var cmt = "<div class = 'row'><br />" +
                            //            "<div class='col-md-8'><textarea rows="5" cols="6"  id = 'txtarea' ></textarea></div><br /> " + 
                            //            "<div class='col-md-4'><button type = 'submit' class ='btn btn-primary form-control' value='Add Comment' /></div></div>";
                            document.getElementById("cbranch").innerHTML = course;
                            document.getElementById("creviews").innerHTML = review; // + "<br />"+ cmt;
                            
                            google.charts.load('current', {'packages':['corechart']});
                            google.charts.setOnLoadCallback(drawChart);
                            // Draw the chart and set the chart values
                            function drawChart() {
                              var data = google.visualization.arrayToDataTable([
                              ['Sentiment', 'No of Review'],
                              ['Negative Reviews', 2.5 * neg],
                              ['Positive Reviews', 2.5 * pos]
                            ]);

                              // Optional; add a title and set the width and height of the chart
                            var options = {
                                  title:'Review Summary', 
                                  width:500, 'height':350,
                                  slices: {
                                      0: {color:'Red'},
                                      1: {color: 'Green'}
                                  }
                                  //sliceVisibilityThreshold: 0.5
                              };

                              // Display the chart inside the <div> element with id="piechart"
                              var chart = new google.visualization.PieChart(document.getElementById('piechart'));
                              chart.draw(data, options);
                            }
                            
                            
                    } else {
                            //document.getElementById("cdetail").innerHTML = "LOADING...";
                    }
                };
                xmlhttp.open("POST","http://localhost:8080/CollegeReview/CollegeDetails?collegeName="+ name, true);
                xmlhttp.send();
            }
            </script>
            <script>
            $(document).ready(function () {
                $('#sidebar').niceScroll({
                    cursorcolor: '#53619d', // Changing the scrollbar color
                    cursorwidth: 0, // Changing the scrollbar width
                    cursorborder: 'none' // Removing the scrollbar border
                });
            });
            </script>
        
        <!-- Bootstrap CSS CDN -->
        <!--<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">-->
        <link rel="stylesheet" href="css/bootstrap.min.css" />
        <!-- Our Custom CSS -->
        <link rel="stylesheet" href ="css/modal.css" />
        <link rel="stylesheet" href="css/homepage.css" />
    </head>
    <body>
        <div class="wrapper">
            <!-- Sidebar Holder -->
            <nav id="sidebar">
                <div class="sidebar-header">
                    <h3>CollegeDB</h3>
                </div>
                <form method="POST" action="DBNegotiator">
                    <div class="form-group">
                        <div class="input-group margin-bottom-sm">
                            <input type="text" id="clgName" autocomplete="off" class="col-xs-4" style="margin-left: 5px;padding: 0px 10px; width: 375px; height: 45px;" placeholder="Search College" onkeyup="getData(this.value)"/>
                            <span class="input-group-addon" id="simg" style="background-color: white; " onclick="getData(document.getElementById('clgName').value)"><i class="fa fa-search fa-lg" aria-hidden="true"></i></span>
                            <!--<button id="search" type="button"  style="width: 45px;" class="btn btn-default" onclick="getData(document.getElementById('clgName').value)">
                                    
                            </button>-->
                        </div>
                    </div>
                </form>
                <form id = "clg" method="POST" action="CollegeDetails">
                    <ul class="list-unstyled components">
                        <li>
                            <a href="#">About</a>
                            <!--
                            <a href="#pageSubmenu" data-toggle="collapse" aria-expanded="false">Pages</a>
                            <ul class="collapse list-unstyled" id="pageSubmenu">
                                <li><a href="#">Page 1</a></li>
                                <li><a href="#">Page 2</a></li>
                                <li><a href="#">Page 3</a></li>
                            </ul>
                            -->
                        </li>
                        <li>
                            <a href="#">Portfolio</a>
                        </li>
                        <li>
                            <a href="#">Contact</a>
                        </li>
                        <li id = "val">
                            
                        </li>
                        
                    </ul>
                    
                </form>
                    
            </nav>
            
            <!-- Page Content Holder -->
            <div id="content">
                
                <nav class="navbar navbar-default navbar-fixed-top">
                    <div class="container-fluid">

                        <div class="navbar-header">
                            <button type="button" id="sidebarCollapse" class="btn btn-info navbar-btn">
                                <i class="glyphicon glyphicon-align-left"></i>
                                <span>Toggle Sidebar</span>
                            </button>
                        </div>
                        <!--
                        <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                            <ul class="nav navbar-nav navbar-right">
                                <li><a href="#">Page</a></li>
                                <li><a href="#">Page</a></li>
                                <li><a href="#">Page</a></li>
                                <li><a href="#">Page</a></li>
                            </ul>
                        </div>
                        -->
                        
                    </div>
                </nav>
                <div class="jumbotron">
                    <h1 id ="cname">CollegeDB | A place to share your views</h1>
                    <h4 id="saddr"></h4>
                    <div class="row">
                        <div class="col-md-6" id ="piechart"></div>
                    </div>
                </div>
                <div class="line"></div>
                <div class="line"></div>
                <div class="container">
                    
                    <!-- The Modal -->
                    <div id="myModal" class="modal">

                      <!-- Modal content -->
                      <div class="modal-content">
                        <div class="modal-header">
                          
                          <h2>Review Sentiment Result</h2>
                          <span class="close">&times;</span>
                        </div>
                        <div class="modal-body">
                          <p>Review sentiment predicted as: </p>
                          <p id = 'sentiment'>Analyzing Review...</p>
                        </div>
                        <div class="modal-footer">
                          <h3>Is this correct?</h3>
                          <button type='button' id="yes" class="btn btn-info" disabled="true" onclick="prediction(true)">Yes</button>
                          <button type='button' id="no" class="btn btn-danger" disabled="true" onclick="prediction(false)">No</button>
                        </div>
                      </div>

                    </div>
                                        
                    <div class="row">
                        <div class="col-md-6" id="cbranch"></div>
                        <div class="col-md-6" id="caddr"></div>
                    </div>
                    <div class="row" id="creviews">
                    </div>
                    <form action="ReviewPredictor" method="POST">
                        <div class = 'row tp hide'>
                            <div class='col-md-8' style="padding-left: 0px"><span><textarea  id = 'comment' rows = '4' class="form-control" style="min-width: 100%" placeholder="Write your review"></textarea></span></div>
                        </div>
                        <div class = 'row tp hide' >
                            <div class='col-sm-3' style="padding-left: 0px">
                                <button type = "button" style="min-width: 100%" id = "cmt" class ="btn btn-primary form-control"  onclick="openModal()"><!--onclick="addComment(document.getElementById('comment'))">-->Add Comment</button>
                            </div>
                        </div>
                    </form>
                </div>
                </div>
        </div>
                 <!-- jQuery CDN -->
         <script src="js/jquery-3.2.1.min.js"></script>
         <!-- Bootstrap Js CDN -->
         <script src="js/bootstrap.min.js"></script>
         <!-- jQuery Nicescroll CDN -->
         <script src="js/jquery.nicescroll.min.js"></script>

         <script type="text/javascript">
             $(document).ready(function () {
                 $("#sidebar").niceScroll({
                     cursorcolor: '#53619d',
                     cursorwidth: 4,
                     cursorborder: 'none'
                 });

                 $('#sidebarCollapse').on('click', function () {
                     $('#sidebar, #content').toggleClass('active');
                     $('.collapse.in').toggleClass('in');
                     $('a[aria-expanded=true]').attr('aria-expanded', 'false');
                 });
             });
         </script>




       
    </body>
</html>
