<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>TODO</title>

    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">

    <!-- jQuery library -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>

    <!-- Latest compiled JavaScript -->
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>


    <script src='http://cdnjs.cloudflare.com/ajax/libs/jquery/2.1.3/jquery.min.js'></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/jquery-confirm/3.3.0/jquery-confirm.min.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-confirm/3.3.0/jquery-confirm.min.js"></script>
    <script src='https://cdn.jsdelivr.net/npm/jquery-validation@1.17.0/dist/jquery.validate.min.js'></script>

    <!--<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/meyer-reset/2.0/reset.min.css">-->

    <link rel='stylesheet prefetch'
          href='https://fonts.googleapis.com/css?family=Roboto:400,100,300,500,700,900|RobotoDraft:400,100,300,500,700,900'>
    <link rel='stylesheet prefetch' href='https://maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css'>

    <style>
        body {
            background: #e9e9e9;
            color: #666666;
            font-family: 'RobotoDraft', 'Roboto', sans-serif;
            font-size: 14px;
            -webkit-font-smoothing: antialiased;
            -moz-osx-font-smoothing: grayscale;
        }

        /* Pen Title */
        .pen-title {
            padding: 50px 0;
            text-align: center;
            letter-spacing: 2px;
        }

        .pen-title h1 {
            margin: 0 0 20px;
            font-size: 48px;
            font-weight: 300;
        }

        .pen-title span {
            font-size: 12px;
        }

        .pen-title span .fa {
            color: #33b5e5;
        }

        .pen-title span a {
            color: #33b5e5;
            font-weight: 600;
            text-decoration: none;
        }

        /* Form Module */
        .form-module {
            position: relative;
            background: #ffffff;
            max-width: 320px;
            width: 100%;
            border-top: 5px solid #33b5e5;
            -webkit-box-shadow: 0 0 3px rgba(0, 0, 0, 0.1);
            box-shadow: 0 0 3px rgba(0, 0, 0, 0.1);
            margin: 0 auto;
        }

        .form-module .toggle {
            cursor: pointer;
            position: absolute;
            top: -0;
            right: -0;
            background: #33b5e5;
            width: 30px;
            height: 30px;
            margin: -5px 0 0;
            color: #ffffff;
            font-size: 12px;
            line-height: 30px;
            text-align: center;
        }

        .form-module .toggle .tooltip {
            position: absolute;
            top: 5px;
            right: -65px;
            display: block;
            background: rgba(0, 0, 0, 0.6);
            width: auto;
            padding: 5px;
            font-size: 10px;
            line-height: 1;
            text-transform: uppercase;
        }

        .form-module .toggle .tooltip:before {
            content: '';
            position: absolute;
            top: 5px;
            left: -5px;
            display: block;
            border-top: 5px solid transparent;
            border-bottom: 5px solid transparent;
            border-right: 5px solid rgba(0, 0, 0, 0.6);
        }

        .form-module .form {
            display: none;
            padding: 40px;
        }

        .form-module .form:nth-child(2) {
            display: block;
        }

        .form-module h2 {
            margin: 0 0 20px;
            color: #33b5e5;
            font-size: 18px;
            font-weight: 400;
            line-height: 1;
        }

        .form-module input {
            outline: none;
            display: block;
            width: 100%;
            border: 1px solid #d9d9d9;
            margin: 0 0 20px;
            padding: 10px 15px;
            -webkit-box-sizing: border-box;
            box-sizing: border-box;
            font-wieght: 400;
            -webkit-transition: 0.3s ease;
            transition: 0.3s ease;
        }

        .form-module input:focus {
            border: 1px solid #33b5e5;
            color: #333333;
        }

        .form-module button {
            cursor: pointer;
            background: #33b5e5;
            width: 100%;
            border: 0;
            padding: 10px 15px;
            color: #ffffff;
            -webkit-transition: 0.3s ease;
            transition: 0.3s ease;
        }

        .form-module button:hover {
            background: #178ab4;
        }

        .form-module .cta {
            background: #f2f2f2;
            width: 100%;
            padding: 15px 40px;
            -webkit-box-sizing: border-box;
            box-sizing: border-box;
            color: #666666;
            font-size: 12px;
            text-align: center;
        }

        .form-module .cta a {
            color: #333333;
            text-decoration: none;
        }

    </style>


</head>

<body>

<h1>Projects</h1>
<!-- Form Module-->
<div class="module form-module">

    <div class="form" style="padding-top: toggle,
            padding-bottom: toggle">
        <h2>Select or Create Project</h2>
        <form id="loginForm" action="create">
            <#--<input type="hidden" value="${context.userId}"/>-->
            <input type="text" name="name" placeholder="Project Name"/>
            <button type="submit">Create New Project</button>
        </form>
    </div>

    <div id="projectsDiv">
        ${context.projects}
    </div>
</div>

<script type="text/javascript">
    // Toggle Function
    $(document).ready(function () {
        $('.form').animate({
            height: "toggle",
            'padding-top': 'toggle',
            'padding-bottom': 'toggle',
            opacity: "toggle"
        }, "slow");
    });

    $('#loginForm').validate({
        submitHandler: function (form) {
            var username = $('#username').val();
            var password = $('#password').val();
            if (!username) {
                alert('Enter username');
            }
            if (!password) {
                alert('Enter Password');
                return;
            }
            form.submit();
        }
    });

</script>

</body>
</html>
