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
            <div class="welcometitle">@lang_docs_title@</div>
            <div class="welcometext">@lang_docs_desc@</div>
        </span>
    </div>
</div>

<div class="container">

<!-- left column -->
<div class="span-16">
    <div class="boxtitle">Dokumenty do pobrania</div>
    <div class="boxpanel">
    	<a href="https://github.com/f-inwest/files/blob/master/dokumenty/Materialy_A.xls?raw=true"><span class="footerlink hoverlink">Zamierzasz zaciągnąć kredyt - skorzystaj z symulacji kredytowej</span></a></br>
    	<a href="https://github.com/f-inwest/files/blob/master/dokumenty/Materialy_C.doc?raw=true"><span class="footerlink hoverlink">Zakwalifikuj wstępnie swoje przedsiębiorstwo zanim pójdziesz do biura rachunkowego</span></a></br>
    	<a href="https://github.com/f-inwest/files/blob/master/dokumenty/Materialy_D.doc?raw=true"><span class="footerlink hoverlink">Czy znasz kalendarium ważnych terminów dla podatnika?</span></a></br>
    	<a href="https://github.com/f-inwest/files/blob/master/dokumenty/Materialy_E.doc?raw=true"><span class="footerlink hoverlink">Nie jesteś pewien swoich rozwiązań podatkowych ...  możesz uzyskać informację na temat Twojej przyszłej sprawy podatkowej</span></a></br>
    	<a href="https://github.com/f-inwest/files/blob/master/dokumenty/Materialy_F.doc?raw=true"><span class="footerlink hoverlink">Czy orientujesz się jakie będą wstępne koszty założenia Twojego przedsiębiorstwa?</span></a></br>
    	<a href="https://github.com/f-inwest/files/blob/master/dokumenty/Materialy_G.doc?raw=true"><span class="footerlink hoverlink">Jeżeli myślisz o założeniu spółki - wybierz odpowiednią dla Siebie i wspólników</span></a></br>
    	<a href="https://github.com/f-inwest/files/blob/master/dokumenty/Materialy_H.doc?raw=true"><span class="footerlink hoverlink">Sprawdź już dziś jakie obowiązują Cię "jako przedsiębiorcę" limity kwotowe</span></a></br>
    	<a href="https://github.com/f-inwest/files/blob/master/dokumenty/Materialy_I.doc?raw=true"><span class="footerlink hoverlink">Sprawdź czy znasz te pojęcia, mogą być Ci pomocne</span></a></br>
    	<a href="https://github.com/f-inwest/files/blob/master/dokumenty/Materialy_J.doc?raw=true"><span class="footerlink hoverlink">Czy wiesz w jakim miesiącu otworzyć firmę, aby zrobić optymalny start?</span></a></br>
    </div>

</div> <!-- end left column -->

<!-- right column -->
<div class="span-8 last">
    <div class="boxtitle header-sidebox">Informacja</div>
    <div class="sidebox">
        <p>Zawartość dokumentów dostępnych na tej stronie ma charakter informacyjny, nie należy ich traktować jako porady prawnej lub podatkowej.</p>
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
