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
        			<div class="pricepoint-description-company"><center>@lang_sms_bottom_note@</center></div>
        		</form>
        	</div>
        </div>
    </div>

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
