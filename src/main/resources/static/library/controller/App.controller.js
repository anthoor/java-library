sap.ui.define(
    ["sap/ui/core/mvc/Controller",
        "sap/ui/model/json/JSONModel",
        "sap/m/MessageToast",
        "sap/ui/thirdparty/jquery",
        "sap/ui/core/Fragment"
    ], function (Controller, JSONModel, MessageToast, jQuery, Fragment) {
        "use strict";
        return Controller.extend("Library.controller.App", {

            onInit: function () {
                this.oController = this;
                this.getView().setModel(new JSONModel("/book/all"), "default");
                this.getView().setModel(new JSONModel("/book/all"));
                this.getView().setModel(new JSONModel("/lease/all"), "lease");
                this.getView().setModel(new JSONModel("/lease/all"), "leaseDefault");
                this.getView().setModel(new JSONModel(), "leaseBook");
                this.getView().setModel(new JSONModel({publishers: [], authors: [], categories: []}), "addBook");
                this.getView().setModel(new JSONModel(), "addAuthor");
                this.getView().setModel(new JSONModel(), "addPublisher");
                this.getView().setModel(new JSONModel(), "addCategory");
                this.getView().setModel(new JSONModel("/author"), "authors");
                this.getView().setModel(new JSONModel("/author"), "authorsDefault");
                this.getView().setModel(new JSONModel("/publisher"), "publishers");
                this.getView().setModel(new JSONModel("/publisher"), "publishersDefault");
                this.getView().setModel(new JSONModel("/category"), "categories");
                this.getView().setModel(new JSONModel("/category"), "categoriesDefault");
            },

            refreshCaches: function () {
                const uniqueNames = this.getView().getModel("leaseDefault").getData().leases.map((item) => item.leasedPerson).reduce((final, current) => {
                    if (!final.includes(current)) {
                        final.push(current);
                    }
                    return final;
                }, []).map((item) => {
                    return {name: item};
                });
                this.getView().setModel(new JSONModel({uniqueNames: uniqueNames}), "uniqueNames");

                const availableBooks = this.getView().getModel().getData().books.filter((item) => item.availability === "Available").map((item) => {
                    return {
                        id: item.id,
                        title: item.title
                    };
                });
                this.getView().setModel(new JSONModel({availableBooks: availableBooks}), "availableBooks");
            },

            onAfterRendering: function () {
                setTimeout(jQuery.proxy(this.refreshCaches, this), 2000);
            },

            onChange: function (oEvent) {
                const filterBy = oEvent.mParameters.newValue;
                if (filterBy) {
                    const books = this.getView().getModel().getData().books;
                    const filteredBooks = {
                        books: books.filter((item) => {
                            const titleMatch = item.title.toUpperCase().includes(filterBy.toUpperCase());
                            const yearMatch = item.year.includes(filterBy);
                            const categoryMatch = item.categories.join(",").toUpperCase().includes(filterBy.toUpperCase());
                            const authorMatch = item.authors.join(",").toUpperCase().includes(filterBy.toUpperCase());
                            const publisherMatch = item.publishers.join(",").toUpperCase().includes(filterBy.toUpperCase());
                            return titleMatch || yearMatch || categoryMatch || authorMatch || publisherMatch;
                        })
                    };
                    this.getView().setModel(new JSONModel(filteredBooks), "default");
                } else {
                    this.getView().setModel(this.getView().getModel(), "default");
                }
            },

            handleActionPress: function (oEvent) {
                const rowId = oEvent.getParameter("id").split("-").slice(-1)[0];
                const bindingPath = "/leases/" + rowId;
                const bindingData = this.getView().getModel("lease").getObject(bindingPath);
                const requestUrl = `/lease/${bindingData.id}/book/${bindingData.book.id}`;
                const refreshData = this.onAfterRendering.bind(this);
                jQuery.ajax(requestUrl, {
                    method: "PUT",
                    contentType: "application/json",
                    success: (data) => {
                        this.getView().setModel(new JSONModel("/book/all"), "default");
                        this.getView().setModel(new JSONModel("/book/all"));
                        this.getView().setModel(new JSONModel("/lease/all"), "lease");
                        this.getView().setModel(new JSONModel("/lease/all"), "leaseDefault");
                        this.getView().setModel(new JSONModel(), "leaseBook");
                        setTimeout(refreshData, 2000);
                        MessageToast.show(`Returned book ${bindingData.book.title} to library`);
                    },
                    error: (err) => {
                        MessageToast.show("Something went wrong.");
                    }
                });
            },

            showLeaseBookDialog: function () {
                var oView = this.getView();

                // create dialog lazily
                if (!this.leaseDialog) {
                    this.leaseDialog = Fragment.load({
                        id: oView.getId(),
                        name: "Library.view.fragments.Lease",
                        controller: this
                    }).then(function (oDialog) {
                        oView.addDependent(oDialog);
                        return oDialog;
                    });
                }

                this.leaseDialog.then(function (oDialog) {
                    oDialog.open();
                });
            },

            onCloseDialog: function () {
                const data = this.getView().getModel("leaseBook").getData();
                const payload = {
                    "leasedPerson": data.leasedPerson,
                    "leasedOn": data.leasedOn.split("/").reverse().join("-"),
                    "book": {
                        "bookId": data.bookId
                    }
                };
                const refreshData = this.onAfterRendering.bind(this);
                jQuery.ajax("/lease", {
                    method: "POST",
                    data: JSON.stringify(payload),
                    contentType: "application/json",
                    success: (data) => {
                        this.getView().setModel(new JSONModel("/book/all"), "default");
                        this.getView().setModel(new JSONModel("/book/all"));
                        this.getView().setModel(new JSONModel("/lease/all"), "lease");
                        this.getView().setModel(new JSONModel("/lease/all"), "leaseDefault");
                        this.getView().setModel(new JSONModel(), "leaseBook");
                        setTimeout(refreshData, 2000);
                        this.byId("leaseDialog").close();
                        MessageToast.show(`Book leased successfully`);
                    },
                    error: (err) => {
                        MessageToast.show("Something went wrong");
                    }
                });
                this.byId("leaseDialog").close();
            },

            onCancelDialog: function () {
                this.byId("leaseDialog").close();
            },

            onAddAuthorPress: function (oEvent) {
                var oView = this.getView();

                if (!this.addAuthorDialog) {
                    this.addAuthorDialog = Fragment.load({
                        id: oView.getId(),
                        name: "Library.view.fragments.AddAuthor",
                        controller: this
                    }).then(function (oDialog) {
                        oView.addDependent(oDialog);
                        return oDialog;
                    });
                }

                this.addAuthorDialog.then(function (oDialog) {
                    oDialog.open();
                });
            },

            onAddAuthorOk: function (oEvent) {
                const data = this.getView().getModel("addAuthor").getData();
                jQuery.ajax('/author', {
                    method: "POST",
                    data: JSON.stringify(data),
                    contentType: "application/json",
                    success: (data) => {
                        this.getView().setModel(new JSONModel("/author"), "authors");
                        this.getView().setModel(new JSONModel("/author"), "authorsDefault");
                        this.getView().setModel(new JSONModel(), "addAuthor");
                        this.byId("addAuthorDialog").close();
                        MessageToast.show("Author added");
                    },
                    error: (err) => {
                        MessageToast.show("Something went wrong");
                    }
                });
            },

            onAddAuthorCancel: function (oEvent) {
                this.byId("addAuthorDialog").close();
            },

            onAddPublisherPress: function (oEvent) {
                var oView = this.getView();

                if (!this.addPublisherDialog) {
                    this.addPublisherDialog = Fragment.load({
                        id: oView.getId(),
                        name: "Library.view.fragments.AddPublisher",
                        controller: this
                    }).then(function (oDialog) {
                        oView.addDependent(oDialog);
                        return oDialog;
                    });
                }

                this.addPublisherDialog.then(function (oDialog) {
                    oDialog.open();
                });
            },

            onAddPublisherOk: function (oEvent) {
                const data = this.getView().getModel("addPublisher").getData();
                jQuery.ajax('/publisher', {
                    method: "POST",
                    data: JSON.stringify(data),
                    contentType: "application/json",
                    success: (data) => {
                        this.getView().setModel(new JSONModel("/publisher"), "publishers");
                        this.getView().setModel(new JSONModel("/publisher"), "publishersDefault");
                        this.getView().setModel(new JSONModel(), "addPublisher");
                        this.byId("addPublisherDialog").close();
                        MessageToast.show("Publisher added");
                    },
                    error: (err) => {
                        MessageToast.show("Something went wrong");
                    }
                });
            },

            onAddPublisherCancel: function (oEvent) {
                this.byId("addPublisherDialog").close();
            },

            onAddBookPress: function (oEvent) {
                var oView = this.getView();

                if (!this.addBookDialog) {
                    this.addBookDialog = Fragment.load({
                        id: oView.getId(),
                        name: "Library.view.fragments.AddBook",
                        controller: this
                    }).then(function (oDialog) {
                        oView.addDependent(oDialog);
                        return oDialog;
                    });
                }

                this.addBookDialog.then(function (oDialog) {
                    oDialog.open();
                });
            },

            onAddBookOk: function (oEvent) {
                const data = this.getView().getModel("addBook").getData();
                const oController = this;
                const payload = {
                    bookTitle: data.bookTitle,
                    bookYear: data.bookYear,
                    bookShelf: data.bookShelf,
                    bookRow: data.bookRow,
                    bookAvailable: true,
                    authors: data.authors.map(item => {
                        return {
                            authorId: parseInt(item, 10)
                        };
                    }),
                    publishers: data.publishers.map(item => {
                        return {
                            publisherId: parseInt(item, 10)
                        };
                    }),
                    categories: data.categories.map(item => {
                        return {
                            categoryId: parseInt(item, 10)
                        };
                    })
                };
                jQuery.ajax('/book', {
                    method: "POST",
                    data: JSON.stringify(payload),
                    contentType: "application/json",
                    success: (data) => {
                        this.getView().setModel(new JSONModel("/book/all"), "default");
                        this.getView().setModel(new JSONModel("/book/all"));
                        this.getView().setModel(new JSONModel(), "addBook");
                        jQuery.proxy(this.refreshCaches, oController);
                        this.byId("addBookDialog").close();
                        MessageToast.show("Book added");
                    },
                    error: (err) => {
                        MessageToast.show("Something went wrong");
                    }
                });
            },

            handleAuthorSelectionChange: function (oEvent) {
                const changedItem = oEvent.getParameter("changedItem");
                const isSelected = oEvent.getParameter("selected");
                const item = changedItem.getProperty("key");
                let data = this.getView().getModel("addBook").getData();
                if (isSelected) {
                    data.authors.push(item);
                } else {
                    data.authors = data.authors.filter(entry => entry !== item);
                }
                this.getView().getModel("addBook").setData(data);
            },

            handleCategorySelectionChange: function (oEvent) {
                const changedItem = oEvent.getParameter("changedItem");
                const isSelected = oEvent.getParameter("selected");
                const item = changedItem.getProperty("key");
                let data = this.getView().getModel("addBook").getData();
                if (isSelected) {
                    data.categories.push(item);
                } else {
                    data.categories = data.categories.filter(entry => entry !== item);
                }
                this.getView().getModel("addBook").setData(data);
            },

            handlePublisherSelectionChange: function (oEvent) {
                const changedItem = oEvent.getParameter("changedItem");
                const isSelected = oEvent.getParameter("selected");
                const item = changedItem.getProperty("key");
                let data = this.getView().getModel("addBook").getData();
                if (isSelected) {
                    data.publishers.push(item);
                } else {
                    data.publishers = data.publishers.filter(entry => entry !== item);
                }
                this.getView().getModel("addBook").setData(data);
            },

            onAddBookCancel: function (oEvent) {
                this.byId("addBookDialog").close();
            },

            onAddCategoryPress: function (oEvent) {
                var oView = this.getView();

                if (!this.addCategoryDialog) {
                    this.addCategoryDialog = Fragment.load({
                        id: oView.getId(),
                        name: "Library.view.fragments.AddCategory",
                        controller: this
                    }).then(function (oDialog) {
                        oView.addDependent(oDialog);
                        return oDialog;
                    });
                }

                this.addCategoryDialog.then(function (oDialog) {
                    oDialog.open();
                });
            },

            onAddCategoryOk: function (oEvent) {
                const data = this.getView().getModel("addCategory").getData();
                jQuery.ajax('/category', {
                    method: "POST",
                    data: JSON.stringify(data),
                    contentType: "application/json",
                    success: (data) => {
                        this.getView().setModel(new JSONModel("/category"), "categories");
                        this.getView().setModel(new JSONModel("/category"), "categoriesDefault");
                        this.getView().setModel(new JSONModel(), "addCategory");
                        this.byId("addCategoryDialog").close();
                        MessageToast.show("Category added");
                    },
                    error: (err) => {
                        MessageToast.show("Something went wrong");
                    }
                });
            },

            onAddCategoryCancel: function (oEvent) {
                this.byId("addCategoryDialog").close();
            }
        });
    }
);
