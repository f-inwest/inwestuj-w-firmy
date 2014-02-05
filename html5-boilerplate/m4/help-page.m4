`
<!doctype html>
<html lang="en">
'
include(head.m4)
`
<body class="help-page">
<div id="wrap">
<div id="main">
'
include(header.m4)
`
<div class="banner helpbanner">
    <div class="container">
        <span class="bannertext span-24">
            <div class="welcometitle">@lang_help_title@</div>
            <div class="welcometext" style="right: 50px">@lang_help_desc@</div>
        </span>
    </div>
</div>

<div class="container">

<!-- left column -->
<div class="span-16">
    <div class="boxtitle">@lang_help_and_faq@</div>
    <div class="boxpanel">
        <dl>
            <dt>@lang_what_is_site@</dt>
            <dd>@lang_what_is_site_desc@</dd>
            <dt>@lang_who_runs_site@</dt>
            <dd>@lang_who_runs_site_desc@</dd>
            <dt>@lang_how_contact@</dt>
            <dd><a href="contact-page.html">@lang_visit_the_contact_page@</a>.</dd>
            <dt>@lang_what_cost@</dt>
            <dd>@lang_what_cost_desc@</dd>
            <dt>@lang_how_accept_bid@</dt>
            <dd>@lang_how_accept_bid_desc@</dd>
            <dt>@lang_offer_question@</dt>
            <dd>@lang_offer_question_desc@</dd>
        </dl>
    </div>

</div> <!-- end left column -->

<!-- right column -->
<div class="span-8 last">
<!--
    <div class="boxtitle">A PIECE OF THE ACTION</div>
    <div class="sidebox">
        <p>
With Inwestuj w Firmy, you&rsquo;re plugged into the pulse of the startup community.
Keep up to date on all the latest startups.  Post your own startup as an
entrepreneuer, getting feedback and exposure to investors worldwide.  As an
accredited investor, you can bid for a piece of the action.
        </p>
    </div>
-->
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
