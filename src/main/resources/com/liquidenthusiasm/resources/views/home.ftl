<html>
<head>
    <script src="https://code.jquery.com/jquery-2.2.3.min.js" integrity="sha256-a23g1Nt4dtEYOj7bR+vTu7+T8VP13humZFBJNIYoEJo="
            crossorigin="anonymous"></script>
    <script>
        $(function () {
            function flash(msg) {
                $('#flash').html(msg);
            }

            $('form').submit(function (e) {
                e.preventDefault();
                $.post({
                    url: "/app/login",
                    data: JSON.stringify({user: $('#email').val(), pass: $('#pass').val()}),
                    contentType: "application/json"
                }).done(
                        function (data) {
                            window.location.href = "/app/game";
                        })
                        .fail(function (response, status, errorCode) {
                            if (errorCode == 'Unauthorized') {
                                flash('Invalid username/pass!');
                                $('#pass').val('');
                            } else {
                                flash('Sorry, something went wrong while logging in. Please try again in a bit.');
                                $('#pass').val('');
                            }
                        })
                        .always(function () {
                        });

            });
        });
    </script>
</head>
<body>

<h4>Login</h4>

<form method="POST">
    <label>Email: </label><input id="email" type="email" name="email"/><br/>
    <label>Password: </label><input id="pass" type="password" name="pass"/><br/>
    <input type="submit" value="Login"/>
</form>
<div id="flash"></div>

<h4><a href="account/create">Create account</a></h4>
</body>
</html>