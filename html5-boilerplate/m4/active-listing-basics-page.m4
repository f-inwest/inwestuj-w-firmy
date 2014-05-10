`
<!doctype html>
<html lang="@lang_html_language@">
'
include(head.m4)
`
<body class="new-listing-basics-page">
<div id="wrap">
<div id="newlistingmain">
'
include(header.m4)
include(company-not-found.m4)
`
<div class="container preloader">
    <div class="preloaderfloater"></div>
    <div class="preloadericon"></div>
</div>
<div class="container initialhidden wrapper">

<div class="span-16">
    <div class="formitem clear">
        <span class="inputmsg" id="newlistingmsg"></span>
    </div>
</div>

<div class="initialhidden" id="newlistingbasicswrapper">
'
include(basics-editable.m4)
include(basics-rightcol-active.m4)
include(new-listing-bottom-buttons.m4)
`
</div>

</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(footer.m4)
`
<script src="https://maps.googleapis.com/maps/api/js?libraries=places&sensor=true&language=@lang_mapsapi_version@"></script>
<script src="js/modules/base.js"></script>
<script src="js/modules/forms.js"></script>
<script src="js/modules/newlistingbase.js"></script>
<script src="js/modules/newlistingbasicspage.js"></script>
'
include(promptie.m4)
`
</body>
</html>
'
