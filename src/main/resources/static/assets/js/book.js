$('document').ready(function() {

    $('table #editButton').on('click', function(event) {
        var href = $(this).attr('href');
        $.get(href, function(book, status) {
            $('#id-edit').val(book.id);
            $('#title-edit').val(book.title);
            $('#authors-edit').val(book.authors);
            $('#isbn-edit').val(book.isbn);
            $('#language-edit').val(book.language);
            $('#year-of-publication-edit').val(book.yearOfPublication);
            $('#publisher-edit').val(book.publisher);
            $('#number-of-pages-edit').val(book.numberOfPages);
            $('#description-edit').val(book.authors);
            $('#price-edit').val(book.numberOfPages);
        });
        $('#editModal').modal();
    });

    $('table #deleteButton').on('click', function(event) {
        var href=$(this).attr('href');
        $('#confirmDeleteButton').attr('href', href);
        $('#deleteModal').modal();
    });

    $('table #cfButton').on('click', function(event) {
        var href=$(this).attr('href');

        var form = document.getElementById('cfForm') || null;
        if(form) {
           form.action = href;
        }
    });

});