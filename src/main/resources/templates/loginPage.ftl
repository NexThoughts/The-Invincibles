<html>
<head><title></title></head>
<body>

<form action="${context.request().path()}loginAuth" method="post">
    <div>
        <label>EmailId</label>
        <input type="text" id="emailId" name="emailId"/>
    </div>
    <div>
        <label>Password</label>
        <input type="text" id="password" name="password"/>
    </div>
    <div class="button">
        <button type="submit">Submit</button>
    </div>
</form>


</body>
</html>