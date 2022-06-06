$('document').ready(function() {

    $('table #clearCartButton').on('click', function(event) {
        var href=$(this).attr('href');
        $('#confirmClearCartButton').attr('href', href);
        $('clearCartModal').modal();
    });

    $('table #orderButton').on('click', function(event) {
        var href=$(this).attr('href');
        $('#confirmOrderButton').attr('href', href);
        $('orderModal').modal();
    });

});