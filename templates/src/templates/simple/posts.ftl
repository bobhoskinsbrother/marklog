<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
    <style type="text/css">
        body {
            font-family: sans-serif;
            font-size: 0.8em;
        }
    </style>
</head>
<body>
<#list posts as post>
${post}
<#if post_has_next><br/></#if>
</#list>

</body>
</html>