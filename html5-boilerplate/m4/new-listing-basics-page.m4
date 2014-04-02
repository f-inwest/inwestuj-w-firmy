`
<!doctype html>
<html lang="en">
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
<div class="banner genericbanner">
    <div class="container">
        <span class="bannertext span-24">
            <div class="welcometitle">@lang_wizard_title@</div>
            <div class="welcometext">@lang_innovatorsmessage_plain@</div>
        </span>
    </div>
</div>

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
<!-- left column -->
<div class="span-16">

    <div class="boxtitle basicstitle header-nobottom">
        <span class="titletext">@lang_project_information@</span>
        <div class="titleinfobtn"></div>
        <div class="titleinfo">
            <label class="titleinfoheader">@lang_instructions@</label>
            <p>@lang_instructions_desc@</p>
            <p>@lang_instructions_warn@</p>
        </div>
        <span class="bmctitlemsg" id="newlistingbasicsmsg"></span>
    </div>

    <div class="boxpanel newlistingpanel">
        <div class="formitem sideinfoitem clear">
            <label class="inputlabel inputlabel-name" for="title">@lang_name@</label>
            <span class="inputfield">
                <input class="text inputwidetext" type="text" name="title" id="title" value=""></input>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">@lang_name@</label>
                <br />
                @lang_project_name_desc@
            </p>
            <span class="inputicon">
                <div id="titleicon"></div>
            </span>
        </div>
        <div class="formitem sideinfoitem clear">
            <label class="inputlabel" for="type">@lang_type@</label>
            <span class="inputfield">
                <select id="type" class="text inputwidetext categoryselect">
                    <option value="application">Application</option>
                    <option value="company" selected="selected">Company</option>
                </select>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">@lang_type@</label>
                <br />
                @lang_type_desc@
            </p>
            <span class="inputicon">
                <div id="typeicon"></div>
            </span>
        </div>
        <div class="formitem sideinfoitem clear">
            <label class="inputlabel" for="platform">@lang_platform@</label>
            <span class="inputfield">
                <select id="platform" class="text inputwidetext categoryselect">
                    <option value="ios">@lang_ios@</option>
                    <option value="android">@lang_android@</option>
                    <option value="windows_phone">@lang_windows_phone@</option>
                    <option value="desktop">@lang_desktop@</option>
                    <option value="website">@lang_web@</option>
                    <option value="other" selected="selected">@lang_other@</option>
                </select>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">@lang_platform@</label>
                <br />
                @lang_platform_desc@
            </p>
            <span class="inputicon">
                <div id="platformicon"></div>
            </span>
        </div>

        <div class="formitem sideinfoitem clear">
            <label class="inputlabel" for="category">@lang_category@</label>
            <span class="inputfield">
                <select id="category" class="text inputwidetext categoryselect">
                </select>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">@lang_category@</label>
                <br />
                @lang_project_category_desc@
            </p>
            <span class="inputicon">
                <div id="categoryicon"></div>
            </span>
        </div>

        <div class="formitem sideinfoitem clear">
            <label class="inputlabel" for="stage">@lang_project_stage@</label>
            <span class="inputfield">
                <select id="stage" class="text inputwidetext categoryselect">
                    <option value="concept">@lang_concept@</option>
                    <option value="startup" selected="selected">@lang_startup@</option>
                    <option value="established">@lang_established@</option>
                </select>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">@lang_project_stage@</label>
                <br />
                @lang_project_stage_desc@
            </p>
            <span class="inputicon">
                <div id="stageicon"></div>
            </span>
        </div>
<!--
        <div class="formitem sideinfoitem clear">
            <label class="inputlabel" for="contact_email">EMAIL</label>
            <span class="inputfield">
                <input class="text inputwidetext" type="text" name="contact_email" id="contact_email" value=""></input>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">Email</label>
                <br />
                Email address where you want to be contacted regarding this listing.
            </p>
            <span class="inputicon">
                <div id="contact_emailicon"></div>
            </span>
        </div>
-->
        <div class="formitem sideinfoitem clear">
            <label class="inputlabel inputlabel-mantra" for="mantra">@lang_mantra@</label>
            <span class="inputfield">
                <textarea class="inputtextareatwoline inputwidetext" cols="20" rows="5" name="mantra" id="mantra" maxlength="140"></textarea>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">@lang_mantra@</label>
                <br />
                @lang_mantra_desc@
            </p>
            <span class="inputicon">
                <div id="mantraicon"></div>
            </span>
        </div>
        <div class="formitem sideinfoitem clear">
            <label class="inputlabel inputlabel-summary" for="summary">@lang_summary@</label>
            <span class="inputfield">
                <textarea class="inputtextareatwoline inputwidetext inputelevatorpitch" cols="20" rows="5" name="summary" id="summary" maxlength="2000"></textarea>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">@lang_summary@</label>
                <br />
                @lang_summary_desc@
            </p>
            <span class="inputicon">
                <div id="summaryicon"></div>
            </span>
        </div>
        <div class="formitem sideinfoitem clear">
        </div>
    </div>

    <div class="boxtitle basicstitle header-nobottom">
        <span class="titletext">@lang_logo_title@</span>
        <div class="titleinfobtn"></div>
        <div class="titleinfo">
            <label class="titleinfoheader">@lang_logo_title@</label>
            <p>@lang_logo_desc@</p>
        </div>
    </div>

   <div class="boxpanel logopanel">
        <div class="formitem sideinfoitem clear">
            <label class="inputlabel" for="address">@lang_logo@</label>
            <span class="inputfield">
                <div class="tileimg noimage" id="logoimg"></div>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">@lang_logo_title@</label>
                <br />
                @lang_logo_desc@
            </p>
            <span class="inputicon">
                <div id="logoicon"></div>
            </span>
        </div>
        <div class="formitem sideinfoitem clear">
        </div>
    </div>

'
include(images-panel-editable.m4)
`

    <div class="boxtitle basicstitle header-nobottom">
        <span class="titletext">@lang_location_title@</span>
        <div class="titleinfobtn"></div>
        <div class="titleinfo">
            <label class="titleinfoheader">@lang_location_title@</label>
            <p>@lang_location_desc@</p>
        </div>
        <span class="bmctitlemsg" id="newlistingbasicsmsg"></span>
    </div>
    <div class="boxpanel newlistingpanel newlistingpanel-location">
        <div class="formitem sideinfoitem clear">
            <label class="inputlabel inputlabel-location" for="address">@lang_location@</label>
            <span class="inputfield">
                <input class="text inputwidetext" type="text" name="address" id="address" value=""></input>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">@lang_location@</label>
                <br />
                @lang_project_location_desc@
            </p>
            <span class="inputicon">
                <div id="locationicon"></div>
            </span>
        </div>
        <div class="formitem sideinfoitem clear">
        </div>
    </div>

    <div class="formitem clear">
        <span class="inputmsg" id="submiterrormsg"></span>
    </div>

</div> <!-- end left column -->

<!-- right column -->
<div class="span-8 last">

    <div class="sidebox inputbutton previewbutton toppreviewbutton">@lang_preview@ &gt;&gt;</div>

    <a href="/new-listing-financials-page.html">
        <div class="sidebox investbutton" id="askfundingbutton">@lang_ask_for_funding@</div>
    </a>

    <a href="/new-listing-media-page.html">
        <div class="sidebox investbutton" id="videobutton">@lang_add_video@</div>
    </a>

    <a href="/new-listing-valuation-page.html">
        <div class="sidebox investbutton" id="valuationbutton">@lang_add_valuation@</div>
    </a>

    <a href="/new-listing-bmc-page.html">
        <div class="sidebox investbutton" id="modelbutton">@lang_add_model@</div>
    </a>

    <a href="/new-listing-qa-page.html">
        <div class="sidebox investbutton" id="presentationbutton">@lang_add_presentation@</div>
    </a>

    <a href="/new-listing-documents-page.html">
        <div class="sidebox investbutton" id="documentbutton">@lang_add_document@</div>
    </a>

    <div class="boxtitle boxtitleside clear boxtitleside-logo header-nobottom">@lang_upload_logo@</span></div>
    <div class="uploadbox uploadbox-logo">
        <div class="formitem">
            <span class="uploadinfo">@lang_upload_desc@</span>
        </div>
        <div class="formitem clear">
            <span class="inputfield">
                <input class="text picinputlink" type="text" maxlength="255" name="logo_url" id="logo_url" value=""></input>
            </span>
            <span class="uploadinputicon">
                <div id="logo_urlicon"></div>
            </span>
        </div>
        <div class="formitem">
            <span class="inputfield">
                <form id="logouploadform" method="post" enctype="multipart/form-data" target="logouploadiframe" action="#">
                    <input class="text picinputlink" id="logouploadfile" name="LOGO" size="16" type="file"></input>
                    <iframe id="logouploadiframe" name="logouploadiframe" src="" class="uploadiframe"></iframe>
                </form>
            </span>
        </div>
        <div class="formitem clear">
            <span class="uploadinfo" id="logomsg">@lang_upload_logo_short_desc@</span>
        </div>
    </div>

    <div class="boxtitle boxtitleside clear header-nobottom">@lang_upload_image@ <span id="picnum">1</span></div>
    <div class="uploadbox">
        <div class="formitem">
            <span class="uploadinfo">@lang_upload_desc@</span>
        </div>
        <div class="formitem clear">
            <span class="inputfield">
                <input class="text picinputlink" type="text" maxlength="255" name="pic_url" id="pic_url" value=""></input>
            </span>
            <span class="uploadinputicon">
                <div id="pic_urlicon"></div>
            </span>
        </div>
        <div class="formitem">
            <span class="inputfield">
                <form id="picuploadform" method="post" enctype="multipart/form-data" target="picuploadiframe" action="#">
                    <input class="text picinputlink" id="picuploadfile" name="PIC1" size="16" type="file"></input>
                    <iframe id="picuploadiframe" name="picuploadiframe" src="" class="uploadiframe"></iframe>
                </form>
            </span>
        </div>
        <div class="formitem clear">
            <span class="uploadinfo" id="picmsg">@lang_upload_image_short_desc@</span>
        </div>
    </div>

    <div class="boxtitle boxtitleside clear boxtitleside-location header-nobottom">@lang_map@</span></div>
    <div class="sidebox mapsidebox">
        <span class="inputmap" id="addressmap"></span>
    </div>
</div>
</div>

'
include(new-listing-bottom-buttons.m4)
`
<!-- end right column -->


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
