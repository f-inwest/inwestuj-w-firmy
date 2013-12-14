`
<!doctype html>
<html lang="en">
'
include(mainhead.m4)
`
<body class="discover-page">
<div id="fb-root"></div> <!-- for fb like button -->
<div id="wrap">
<div id="main">
'
include(header.m4)
include(banner.m4)
`
<div class="container">

<div class="span-24 preloader">
    <div class="preloaderfloater"></div>
    <div class="preloadericon"></div>
</div>

<div class="span-24 initialhidden wrapper">
    <div class="initialhidden content-sub content-initial" id="existinglisting">
        <div class="header-content">
            <span class="header-title">@lang_pending_listing_msg@</span>
        </div>
        <a class="more more-header">@lang_edit_listing@</a>
    </div>
</div>

<div class="span-16 initialhidden wrapper">
    <div class="boxtitle">@lang_top_listings@</div>
    <div id="top_listings"></div>

    <div class="boxtitle">@lang_just_listed@</div>
    <div id="latest_listings"></div>

    <div id="monitored_listings_wrapper" class="initialhidden">
        <div class="boxtitle">@lang_watching@</div>
        <div id="monitored_listings"></div>
    </div>
 </div> 

<div class="initialhidden wrapper">
'
include(main-rightcol.m4)
`
</div> <!-- end wrapper -->

</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(footer.m4)
`
<script src="js/modules/base.js"></script>
<script>
(new DiscoverPageClass()).loadPage();
</script>
'
include(promptie.m4)
`
</body>
</html>
'
