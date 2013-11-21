<#assign header = post.header/>

<div class="header">
<#if header.title?? && header.title?has_content>
<h1>${convert.toHtml(header.title)}</h1>
</#if>
<#if header.author?? && header.author?has_content><p class="a_bit_smaller">by ${header.author}, <#if header.date??>${header.date?string("dd MMM yyyy")}</#if></p></#if>
</div>

<div class="post">
${convert.toHtml(post.markdown)}
</div>


<div class="post_info">
<#if header.date??><strong>At</strong> ${header.date?string("HH:mm")}<br/></#if>
<#if header.tags?? && header.tags?size != 0><strong>Tags</strong> <#list header.tags as tag>${tag}<#if tag_has_next>, </#if></#list></#if>
</div>

