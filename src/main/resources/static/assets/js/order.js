$('document').ready(function() {

    $('table #cancelButton').on('click', function(event) {
        var href=$(this).attr('href');
        $('#confirmCancelButton').attr('href', href);
        $('cancelModal').modal();
    });

    $('table #acceptButton').on('click', function(event) {
        var href=$(this).attr('href');
        $('#confirmAcceptButton').attr('href', href);
        $('acceptModal').modal();
    });

});