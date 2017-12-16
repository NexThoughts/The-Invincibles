<html>
<head><title></title></head>
<body>

<form action="${context.request().path()}saveUser" method="post">
    <div>
        <label>Name</label>
        <input type="text" id="name" name="name"/>
    </div>
    <div>
        <label>username</label>
        <input type="text" id="username" name="username"/>
    </div>
    <div>
        <label>Address</label>
        <input type="text" id="address" name="address"/>
    </div>
    <div class="button">
        <button type="submit">Submit</button>
    </div>
</form>


</body>
</html>