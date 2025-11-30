const dt = new DataTable("#placementsTable", {
    columnDefs: [
        { orderable: false, targets: "no-sort" }
    ],

    layout: {
        topStart: [
            {
                search: {
                    placeholder: "Search placements..."
                }
            }
        ],
        topEnd: {
            pageLength: {
                menu: [5, 10, 25, 50]
            }
        },
        bottomEnd: {
            paging: {
                buttons: 3
            }
        }
    },

    language: {
        lengthMenu: "Show _MENU_ placements per page",
        info: "Showing _START_ to _END_ of _TOTAL_ placements",
        infoEmpty: "No placements available",
        infoFiltered: "(filtered from _MAX_ total placements)",
        zeroRecords: "No matching placement found",
        paginate: {
            previous: "Prev",
            next: "Next"
        }
    }
});