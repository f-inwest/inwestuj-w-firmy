`
<!doctype html>
<html lang="@lang_html_language@">
'
include(head.m4)
`
<body class="new-listing-documents-page">
<div id="wrap">
<div id="newlistingmain">
'
include(header.m4)
include(company-not-found.m4)
`
<div class="banner genericbanner">
    <div class="container">
        <span class="bannertext span-24">
            <div class="welcometitle">@lang_wizard_title@</div>
            <div class="welcometext">@lang_documents@</div>
        </span>
    </div>
</div>

<div class="container preloader">
    <div class="preloaderfloater"></div>
    <div class="preloadericon"></div>
</div>
<div class="container initialhidden wrapper">
'
include(documents-editable.m4)
`
<!-- right column -->
<div class="span-8 last">
    <a href="#" class="backbuttonlink">
        <span class="push-1 span-3 inputbutton backbutton">
            &lt;&lt; @lang_back@
        </span>
    </a>
</div>

<!-- end right column -->
'
include(new-listing-bottom-buttons.m4)
`

</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(footer.m4)
`
<script src="js/modules/base.js"></script>
<script src="js/modules/forms.js"></script>
<script src="js/modules/newlistingbase.js"></script>
<script src="js/modules/newlistingdocumentspage.js"></script>
'
include(promptie.m4)
`
</body>
</html>
'
