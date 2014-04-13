`
<!doctype html>
<html lang="@lang_html_language@">
'
include(head.m4)
`
<body class="company-page">
<div id="fb-root"></div> <!-- for fb like button -->
<div id="wrap">
<div id="main">
'
include(header.m4)
include(company-not-found.m4)
include(company-banner.m4)
companybannermacro(`', `', `companynavselected', `', `', `', `', `')
`
<div class="container preloader">
     <div class="preloaderfloater"></div>
     <div class="preloadericon"></div>
</div>

<div class="container initialhidden wrapper cashflow-wrapper">
'
include(cashflow.m4)
`
</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(footer.m4)
`
<script src="js/modules/base.js"></script>
<script type="text/javascript" src="https://www.google.com/jsapi"></script>
<script src="js/modules/cashflow.js"></script>
<script>
(new CashFlowPageClass()).load();
</script>
'
include(promptie.m4)
`
</body>
</html>
'
