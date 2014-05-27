`
<!doctype html>
<html lang="@lang_html_language@">
'
include(head.m4)
`
<body class="sms-page">
<div id="wrap">
<div id="main">
'
include(header.m4)
`
<div class="banner aboutbanner">
    <div class="container">
        <span class="bannertext span-24">
            <div class="welcometitle">@lang_smspage_title@</div>
            <div class="welcometext">@lang_smspage_desc@</div>
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
	<div id="pricepoints-wrapper" class="initialhidden company-side-menu-inner company-side-menu-inner-pricepoints" style="display: block;">
        <div class="header-content header-title-sidebox">@lang_sms_project_activate@</div>
        <div id="pricepoints-wrapper-inner" class="">
        	<div>
        		<form accept-charset="utf-8" method="post" action="/system/validate_sms_code.html">
        			<input type="hidden" name="p24_id_sprzedawcy" value="@lang_sms_seller_id@" />
        			<input type="hidden" name="p24_kwota" value="@lang_sms_project_activate_value@" />
        			<div class="pricepoint-description-company">@lang_sms_send_sms@</div>
        			<div class="pricepoint-description-company"><center>@lang_sms_project_activate_desc@</center></div>
        			<div class="pricepoint-description-company">@lang_sms_return_code@</div>
        			<input class="text sideinputtext" type="text" maxlength="35" length="35" name="p24_kod" value="" autocomplete="off">
        			<input type="submit" class="inputbutton purchase-button-company" value="@lang_sms_send@" name="submit">
        			<div id="pricepoint-message" class="pricepoint-message initialhidden"></div>
        			<div class="pricepoint-description-company"><center>@lang_sms_bottom_note@</center></div>
        		</form>
        	</div>
        </div>
    </div>

<!--
	<div id="pricepoints-wrapper" class="initialhidden company-side-menu-inner company-side-menu-inner-pricepoints" style="display: block;">
        <div class="header-content header-title-sidebox">@lang_sms_investor_reg@</div>
        <div id="pricepoints-wrapper-inner" class="">
        	<div>
        		<form accept-charset="utf-8" method="post" action="/system/validate_sms_code.html">
        			<input type="hidden" name="p24_id_sprzedawcy" value="@lang_sms_seller_id@" />
        			<input type="hidden" name="p24_kwota" value="@lang_sms_investor_reg_value@" />
        			<div class="pricepoint-description-company">@lang_sms_send_sms@</div>
        			<div class="pricepoint-description-company"><center>@lang_sms_investor_reg_desc@</center></div>
        			<div class="pricepoint-description-company">@lang_sms_return_code@</div>
        			<input class="text sideinputtext" type="text" maxlength="35" length="35" name="p24_kod" value="" autocomplete="off">
        			<input type="submit" class="inputbutton purchase-button-company" value="@lang_sms_send@" name="submit">
        			<div class="pricepoint-description-company"><center>@lang_sms_bottom_note@</center></div>
        		</form>
        	</div>
        </div>
    </div>
-->

</div> <!-- end right column -->

</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(footer.m4)
`
<script src="js/modules/base.js"></script>
<script>
(new SmsPageClass()).loadPage();
</script>
'
include(promptie.m4)
`
</body>
</html>
'
