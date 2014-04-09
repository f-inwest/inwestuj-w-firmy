`
<!doctype html>
<html lang="en">
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

<!-- left column -->
<div class="span-16 initialhidden" id="newlistingdocumentswrapper">

    <div class="boxtitle basicstitle">
        <span class="titletext">@lang_presentation@</span>
        <div class="titleinfobtn"></div>
        <div class="titleinfo">
            <label class="titleinfoheader">@lang_instructions@</label>
            <p>
                @lang_presentation_document_help@
            </p>
        </div>
    </div>
    <div class="boxpanel">
        <div class="mediacol2">
            <div class="formitem">
                <span class="uploadinfo">@lang_upload_desc@</span>
            </div>
            <div class="formitem clear">
                <span class="inputfield">
                    <input class="text mediainputlink" type="text" maxlength="255" name="presentation_url" id="presentation_url" value=""></input>
                </span>
                <p class="dummy"></p>
                <span class="uploadinputicon">
                    <div id="presentation_urlicon"></div>
                </span>
            </div>
            <div class="formitem uploaditem">
                <span class="inputfield">
                    <form id="presentationuploadform" method="post" enctype="multipart/form-data" target="presentationuploadiframe" action="#">
                        <input class="text uploadinputbutton uploadinputbuttonshort" id="presentationuploadfile" name="PRESENTATION" size="18" type="file"></input>
                        <iframe id="presentationuploadiframe" name="presentationuploadiframe" src="" class="uploadiframe"></iframe>
                    </form>
                </span>
                <a href="" id="presentationdeletelink">
                    <span class="span-3 inputbutton uploaddelete">@lang_delete_file@</span>
                </a>
            </div>
            <div class="formitem clear">
                <span class="uploadinfo" id="presentationmsg">@lang_presentation_document_shorthelp@</span>
            </div>
        </div>
    </div>

    <div class="boxtitle basicstitle">
        <span class="titletext">@lang_business_plan@</span>
        <div class="titleinfobtn"></div>
        <div class="titleinfo">
            <label class="titleinfoheader">@lang_instructions@</label>
            <p>
            @lang_business_plan_document_help@
            </p>
        </div>
    </div>
    <div class="boxpanel">
        <div class="mediacol2">
            <div class="formitem">
                <span class="uploadinfo">@lang_upload_desc@</span>
            </div>
            <div class="formitem clear">
                <span class="inputfield">
                    <input class="text mediainputlink" type="text" maxlength="255" name="business_plan_url" id="business_plan_url" value=""></input>
                </span>
                <p class="dummy"></p>
                <span class="uploadinputicon">
                    <div id="business_plan_urlicon"></div>
                </span>
            </div>
            <div class="formitem uploaditem">
                <span class="inputfield">
                    <form id="business_planuploadform" method="post" enctype="multipart/form-data" target="business_planuploadiframe" action="#">
                        <input class="text uploadinputbutton uploadinputbuttonshort" id="business_planuploadfile" name="BUSINESS_PLAN" size="18" type="file"></input>
                        <iframe id="business_planuploadiframe" name="business_planuploadiframe" src="" class="uploadiframe"></iframe>
                    </form>
                </span>
                <a href="" id="business_plandeletelink">
                    <span class="span-3 inputbutton uploaddelete">@lang_delete_file@</span>
                </a>
            </div>
            <div class="formitem clear">
                <span class="uploadinfo" id="business_planmsg">@lang_business_plan_document_shorthelp@</span>
            </div>
        </div>
    </div>

    <div class="boxtitle basicstitle">
        <span class="titletext">@lang_financial_statements@</span>
        <div class="titleinfobtn"></div>
        <div class="titleinfo">
            <label class="titleinfoheader">@lang_instructions@</label>
            <p>
                @lang_financial_statements_document_help@
            </p>
        </div>
    </div>
    <div class="boxpanel">
        <div class="mediacol2">
            <div class="formitem">
                <span class="uploadinfo">@lang_upload_desc@</span>
            </div>
            <div class="formitem clear">
                <span class="inputfield">
                    <input class="text mediainputlink" type="text" maxlength="255" name="financials_url" id="financials_url" value=""></input>
                </span>
                <p class="dummy"></p>
                <span class="uploadinputicon">
                    <div id="financials_urlicon"></div>
                </span>
            </div>
            <div class="formitem uploaditem">
                <span class="inputfield">
                    <form id="financialsuploadform" method="post" enctype="multipart/form-data" target="financialsuploadiframe" action="#">
                        <input class="text uploadinputbutton uploadinputbuttonshort" id="financialsuploadfile" name="FINANCIALS" size="18" type="file"></input>
                        <iframe id="financialsuploadiframe" name="financialsuploadiframe" src="" class="uploadiframe"></iframe>
                    </form>
                </span>
                <a href="" id="financialsdeletelink">
                    <span class="span-3 inputbutton uploaddelete">@lang_delete_file@</span>
                </a>
            </div>
            <div class="formitem clear">
                <span class="uploadinfo" id="financialsmsg">@lang_financial_statements_document_shorthelp@</span>
            </div>
        </div>
    </div>

    <div>
        <div class="formitem clear">
            <span class="newlistingmsgsmall" id="newlistingmsg">&nbsp;</span>
        </div>
    </div>

</div> <!-- end left column -->

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
