`
<!doctype html>
<html lang="@lang_html_language@">
'
include(head.m4)
`
<body class="about-page">
<div id="wrap">
<div id="main">
'
include(header.m4)
`
<div class="banner aboutbanner">
    <div class="container">
        <span class="bannertext span-24">
            <div class="welcometitle">@lang_about_title@</div>
            <div class="welcometext">@lang_about_desc@</div>
        </span>
    </div>
</div>

<div class="container">

<!-- left column -->
<div class="span-16">
    <div class="boxtitle">@lang_how_it_works@
        <a class="videolink about-videolink" href="https://youtu.be/hq5gaQ1FtAU" id="videolink">(@lang_view_on_youtube@)</a>
    </div>
    <div class="boxpanel-youtube">
   	    <iframe width="626" height="382" src="https://www.youtube.com/embed/hq5gaQ1FtAU" frameborder="0" allowfullscreen></iframe>
    </div>

    <div class="boxtitle">@lang_what_we_do@</div>
    <div class="boxpanel">
        <p>@lang_what_we_do_1@</p>
        <p>@lang_what_we_do_2@</p>
        <p>@lang_what_we_do_3@</p>
    </div>

    <div class="boxtitle">@lang_how_to_post@</div>
    <div class="boxpanel">
        <p>@lang_how_to_post_1@</p>
        <p>@lang_how_to_post_2@</p>
        <p>@lang_how_to_post_3@</p>
    </div>

    <div class="boxtitle">@lang_how_to_bid@</div>
    <div class="boxpanel">
        <p>@lang_how_to_bid_1@</p>
        <p>@lang_how_to_bid_2@</p>
        <p>@lang_how_to_bid_3@</p>
        <p>@lang_how_to_bid_4@</p>
    </div>

</div> <!-- end left column -->

<!-- right column -->
<div class="span-8 last">
    <div class="boxtitle header-sidebox">@lang_piece_action@</div>
    <div class="sidebox">
        <p>@lang_piece_action_desc@</p>
    </div>

    <div class="boxtitle header-sidebox" id="listingstitle"></div>

    <!-- companydiv -->
    <div id="companydiv">
        <div class="span-8 preloaderside">
            <div class="preloaderfloater"></div>
            <div class="preloadericon"></div>
        </div>
    </div>
    <!-- end companydiv -->

</div> <!-- end right column -->

</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(footer.m4)
`
<script src="js/modules/base.js"></script>
<script>
(new InformationPageClass()).loadPage();
</script>
'
include(promptie.m4)
`
</body>
</html>
'
