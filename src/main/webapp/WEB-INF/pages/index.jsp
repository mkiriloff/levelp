<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<head>
    <title>Bootstrap Example</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>

    <script>
        function reload() {
            document.location.reload(true);
        }

        function addAccount() {
            $.ajax({
                type: "POST",
                url: "/add",
                data: ({firstname: $("#accountFistName").val(), lastname: $("#accountLastName").val()}),
                success: function (data) {
                    reload();
                },
                error: function (data) {
                    alert(data.responseText);
                    reload();
                },
            });
        }

        function deleteAccount(id) {
            $.ajax({
                type: "POST",
                url: "/delete",
                data: ({id: id}),
                success: function (data) {
                    reload();
                },
                error: function (data) {
                    alert(data.responseText);
                    reload();
                },
            });
        }

        function additionalSum() {
            $.ajax({
                type: "POST",
                url: "/additionalSum",
                data: ({id: $("#addedtoIdAccount").val(), sum: $("#addedSum").val()}),
                success: function (data) {
                    reload();
                },
                error: function (data) {
                    alert(data.responseText);
                    reload();
                },
            });
        }

        function debitSum() {
            $.ajax({
                type: "POST",
                url: "/debitSum",
                data: ({id: $("#debitfromIdAccount").val(), sum: $("#debitSum").val()}),
                success: function (data) {
                    reload();
                },
                error: function (data) {
                    alert(data.responseText);
                    reload();
                },
            });
        }

        function transfer() {
            $.ajax({
                type: "POST",
                url: "/doTransfer",
                data: ({
                    fromid: $("#transferFromIdAccount").val(),
                    toid: $("#transferToIdAccount").val(),
                    sum: $("#transferSum").val()
                }),
                success: function (data) {
                    reload();
                },
                error: function (data) {
                    alert(data.responseText);
                    reload();
                },
            });
        }

    </script>
</head>
<body>
<div class="container">
    <h2>Accounts list</h2>
    <button type="button" class="btn btn-default action-button" id="button_add_account" data-toggle="modal"
            data-target="#addAccountDialog">Create account
    </button>
    <button type="button" class="btn btn-default action-button" id="button_added_balance" data-toggle="modal"
            data-target="#addedBalanceDialog">Added balance
    </button>
    <button type="button" class="btn btn-default action-button" id="button_debit_balance" data-toggle="modal"
            data-target="#debitAccountDialog">Debit balance
    </button>
    <button type="button" class="btn btn-default action-button" id="button_transfer" data-toggle="modal"
            data-target="#transferDialog">Transfer
    </button>

    <table class="table">
        <thead>
        <tr>
            <th>ID</th>
            <th>Firstname</th>
            <th>Lastname</th>
            <th>Balance</th>
            <th>Action Delete</th>
        </thead>
        <tbody>
        <c:forEach items="${accounts}" var="account">
            <tr>
                <td>${account.getid()}</td>
                <td>${account.firstname}</td>
                <td>${account.lastname}</td>
                <td>${account.balance}</td>
                <td>
                    <a type="button" class="btn btn-default action-button" onclick="deleteAccount(${account.getid()})">Delete</a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

</div>
<div class="modal fade" id="addAccountDialog" role="dialog" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">Create new account</h4>
            </div>
            <div class="modal-body">
                <div class="form-group">
                    <label for="accountFistName">Firstname</label>
                    <input type="text" class="form-control" id="accountFistName">
                </div>
                <div class="form-group">
                    <label for="accountLastName">Lastname</label>
                    <input type="text" class="form-control" id="accountLastName">
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-primary" onclick="addAccount()"
                        data-dismiss="modal">Create Account
                </button>
            </div>
        </div>
    </div>
</div>
<div class="modal fade" id="addedBalanceDialog" role="dialog" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">Added balance</h4>
                <div class="modal-body">
                    <div class="form-group">
                        <label for="addedtoIdAccount">ID account</label>
                        <input type="number" class="form-control" id="addedtoIdAccount">
                    </div>
                    <div class="form-group">
                        <label for="addedSum">Sum</label>
                        <input type="number" class="form-control" id="addedSum">
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                    <button type="button" class="btn btn-primary" onclick="additionalSum()"
                            data-dismiss="modal">Added
                    </button>
                </div>
            </div>
        </div>
    </div>
</div>
<div class="modal fade" id="debitAccountDialog" role="dialog" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">Debit balance</h4>
                <div class="modal-body">
                    <div class="form-group">
                        <label for="debitfromIdAccount">ID account</label>
                        <input type="number" class="form-control" id="debitfromIdAccount">
                    </div>
                    <div class="form-group">
                        <label for="debitSum">Sum</label>
                        <input type="number" pattern="0-9]{1,20}" class="form-control" id="debitSum">
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                    <button type="button" class="btn btn-primary" onclick="debitSum()"
                            data-dismiss="modal">Debit
                    </button>
                </div>
            </div>
        </div>
    </div>
</div>
<div class="modal fade" id="transferDialog" role="dialog" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">Transfer to account</h4>
                <div class="modal-body">
                    <div class="form-group">
                        <label for="transferFromIdAccount">From ID account</label>
                        <input type="number" class="form-control" id="transferFromIdAccount">
                    </div>
                    <div class="form-group">
                        <label for="transferToIdAccount">To ID account</label>
                        <input type="number" class="form-control" id="transferToIdAccount">
                    </div>
                    <div class="form-group">
                        <label for="transferSum">Sum</label>
                        <input type="number" class="form-control" id="transferSum">
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                    <button type="button" class="btn btn-primary" onclick="transfer()"
                            data-dismiss="modal">Transfer
                    </button>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
