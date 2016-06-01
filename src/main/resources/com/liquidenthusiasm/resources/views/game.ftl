<#-- @ftlvariable name="coven" type="com.liquidenthusiasm.domain.Coven" -->
<html>
<head>
    <style>

        .option {
            border: 1px solid black;
            margin: 15px;
            padding: 10px;
            background-color: lightgray;
        }

        .valueChange {
            background-color: gold;
            border-color: goldenrod;
            border: 2px solid;
        }

        .notTriggered {
            opacity: .5;
        }

        .storyConditionSet {
            white-space: nowrap;
        }

        .disabled {
            opacity: 0.55;
        }

        .adminOnly {
            opacity: 0.55;
        }

        .wrapper {
            width: 100%;
            margin: 0 auto;
        }

        .header {
            float: left;
            width: 100%;
            height: 5%;
            background-color: #f4f4f4
        }

        .wrapright {
            float: left;
            width: 100%;
        }

        .right {
            margin-left: 255px;
            height: 90%;
            min-width: 500px;
        }

        .left {
            float: left;
            width: 245px;
            margin-left: -100%;
            height: 90%;
            background-color: lightgray;
        }

        .footer {
            float: left;
            width: 100%;
            height: 5%;
            background-color: #f4f4f4;
        }

        body {
            padding: 0px;
            margin: 0px;
        }

    </style>
    <script src="assets/jquery-2.2.3.min.js"></script>
    <script src="assets/main.js"></script>
    <script src="assets/sidebar.js"></script>
    <script src="assets/header.js"></script>
    <script>
        $(function () {
            renderCurrentCovenStory();
            renderHeader();
            renderSidebar();
        });
    </script>
</head>
<body>
<body>
<div class="wrapper">
    <div class="header">
        header
    </div>
    <div class="wrapright">
        <div class="right" id="mainContent">
            right
        </div>
    </div>
    <div class="left" id="sidebar">
    </div>
    <div class="footer">
        footer
    </div>
</div>
</body>
</html>