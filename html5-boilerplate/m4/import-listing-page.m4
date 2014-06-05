`
<!doctype html>
<html lang="@lang_html_language@">
'
include(head.m4)
`
<body class="import-listing-page">
<div id="wrap">
<div id="newlistingmain">
'
include(header.m4)
`
<div class="container preloader">
    <div class="preloaderfloater"></div>
    <div class="preloadericon"></div>
</div>
<div class="container initialhidden wrapper">

<!-- begin banner -->
    <div class="span-24">
        <div class="formitem clear">
            <span class="inputmsg" id="importlistingmsg"></span>
        </div>
    </div>
<!-- end banner -->

<div class="span-24">

    <div class="addlistingtitle">@lang_import_search_for@ <span id="importcorporapp"></span> @lang_import_on@ <span id="importtype"></span></div>

    <div class="addlistinginput">
        <div class="addlistingsearchcontainer">
            <input class="text addlistingsearch" type="text" name="query" id="importquery" value="@lang_import_search@"></input>
            <div class="addlistingsearchbutton" id="importbutton"></div>
<!--            <input class="addlistingsearchbutton" width="32px" type="image" height="32px" src="/img/icons/search-inverted-32.png" alt="search" id="importbutton"> -->
        </div>
    </div>

    <div class="addlistingtitle initialhidden" id="importsfoundtitle">@lang_import_click_your@ <span id="importcorporapp2"></span> @lang_import_below_to_import@</div>

    <div class="addlistingcontainer initialhidden" id="importcontainer">
        <div class="addlistinglist" id="importlist"></div> 
    </div>

    <div class="addlistingtitle">@lang_import_or@</div>
    
    <a href="/new-listing-basics-page.html">
        <div class="addlistingbutton investbutton">@lang_import_create@</div>
    </a>

    <div class="addlistingtitle addlistingbottom">@lang_import_new_listing@</div>

</div> <!-- end span-24 -->
</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(footer.m4)
`
<script src="js/modules/base.js"></script>
<script src="js/modules/importlistingpage.js"></script>
'
include(promptie.m4)
`
</body>
</html>
'
