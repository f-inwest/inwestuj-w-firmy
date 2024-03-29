`
<!doctype html>
<html lang="@lang_html_language@">
'
include(head.m4)
`
<body class="messages-page">
<div id="wrap">
<div id="main">
'
include(header.m4)
`
<div class="container">

<div class="span-24 preloader">
    <div class="preloaderfloater"></div>
    <div class="preloadericon"></div>
</div>

<div class="span-24 initialhidden wrapper">

    <div class="header-content header-boxpanel-initial header-boxpanel-full">
        <div class="header-title">@lang_your_conversations_with@ <span id="from_user_nickname_upper"></span></div>
    </div>
    <div class="boxpanel boxpanelfull" id="messagelistparent">

        <div class="commentline addcommentline initialhidden" id="messagesend">
            <textarea class="textarea commenttextarea"
                id="messagetext" name="messagetext" cols="20" rows="5">@lang_put_comment_here@</textarea>
            <div class="addcommentspinner preloadericon initialhidden" id="messagespinner"></div>
            <span class="span-3 inputbutton messagebutton commentreplybutton" id="messagebtn">@lang_send@</span>
            <p class="commenttext" id="messagemsg"></p>
        </div>

    </div>

</div>
</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(footer.m4)
`
<script src="js/modules/base.js"></script>
<script src="js/modules/messages.js"></script>
<script>
(new MessagePageClass()).loadPage();
</script>
'
include(promptie.m4)
`
</body>
</html>
'
