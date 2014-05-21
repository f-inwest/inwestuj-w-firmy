`
<!doctype html>
<html lang="@lang_html_language@">
'
include(head.m4)
`
<body class="terms-page">
<div id="wrap">
<div id="main">
'
include(header.m4)
`
<div class="banner termsbanner">
    <div class="container">
        <span class="bannertext span-24">
            <div class="welcometitle">@lang_terms_title@</div>
            <div class="welcometext">@lang_terms_desc@</div>
        </span>
    </div>
</div>

<div class="container">

<!-- left column -->
<div class="span-16">

    <div class="boxtitle">@lang_sms_reg_intro@</div>
    <div class="boxpanel">
        @lang_sms_reg_intro_text@
    </div>
    
    <div class="boxtitle">@lang_sms_reg_1@</div>
    <div class="boxpanel">
        @lang_sms_reg_1_text@
    </div>

    <div class="boxtitle">@lang_sms_reg_2@</div>
    <div class="boxpanel">
        @lang_sms_reg_2_text@
    </div>

    <div class="boxtitle">@lang_sms_reg_3@</div>
    <div class="boxpanel">
        @lang_sms_reg_3_text@
    </div>

    <div class="boxtitle">@lang_sms_reg_4@</div>
    <div class="boxpanel">
        @lang_sms_reg_4_text@
    </div>

    <div class="boxtitle">@lang_sms_reg_5@</div>
    <div class="boxpanel">
        @lang_sms_reg_5_text@
    </div>

    <div class="boxtitle">@lang_sms_reg_6@</div>
    <div class="boxpanel">
        @lang_sms_reg_6_text@
    </div>

    <div class="boxtitle">@lang_sms_reg_7@</div>
    <div class="boxpanel">
        @lang_sms_reg_7_text@
    </div>

    <div class="boxtitle">@lang_sms_reg_8@</div>
    <div class="boxpanel">
        @lang_sms_reg_8_text@
    </div>

    <div class="boxtitle">@lang_sms_reg_9@</div>
    <div class="boxpanel">
        @lang_sms_reg_9_text@
    </div>

    <div class="boxtitle">@lang_sms_reg_10@</div>
    <div class="boxpanel">
        @lang_sms_reg_10_text@
    </div>

    <div class="boxtitle">@lang_sms_reg_11@</div>
    <div class="boxpanel">
        @lang_sms_reg_11_text@
    </div>

</div> <!-- end left column -->

<!-- right column -->
<div class="span-8 last">
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
