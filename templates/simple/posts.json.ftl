<#assign count = 0/>
{
"last_updated":${last_updated?string.computer},
"posts": [
<#list posts as post>
    {
        "id":${count},
        "link": "${link_resolver.resolve(post)}",
        "author": "${post.header.author?json_string}",
        "date": "${post.header.date?string("dd/MM/yyyy HH:mm")}",
        "stage": "${post.header.stage?json_string}",
        "tags": "<#list post.header.tags as tag>${tag?json_string}<#if tag_has_next>,</#if></#list>",
        "title": "${post.header.title?json_string}",
        "markdown": "${post.markdown?json_string}"
    }
    <#if post_has_next>,</#if>
<#assign count = (count + 1) />
</#list>
]
}