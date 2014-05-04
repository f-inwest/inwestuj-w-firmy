`
<!doctype html>
<html lang="@lang_html_language@">
'
include(head.m4)
`
<body class="profile-list-page">
<div id="wrap">
<div id="main">
'
include(header.m4)
`
<div class="banner termsbanner">
    <div class="container">
        <span class="bannertext span-24">
            <div class="welcometitle"><span id="typetitle"></span> @lang_on_site@</div>
            <div class="welcometext initialhidden" id="listersubtitle">@lang_listers_desc@</div>
            <div class="welcometext initialhidden" id="dragonsubtitle">@lang_investors_desc@</div>
        </span>
    </div>
</div>
'
include(not-found.m4)
`
<div class="container preloader">
    <div class="preloaderfloater"></div>
    <div class="preloadericon"></div>
</div>
<div class="container initialhidden wrapper">

<div class="span-24">
    <div id="profilelistmsg"></div>

    <div class="addlistingcontainer" id="profilelistcontainer">
        <div class="profilelist" id="profilelist"></div>
    </div>
</div> <!-- end span-24 -->

</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(footer.m4)
`
<script src="js/modules/base.js"></script>
<script src="js/modules/profile.js"></script>
<script>
(new ProfileListClass()).loadPage();
</script>
'
include(promptie.m4)
`
</body>
</html>
'
