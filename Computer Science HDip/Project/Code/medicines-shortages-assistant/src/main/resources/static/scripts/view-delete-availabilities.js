function showConfirmModal(message, onConfirm) {
    // check if modal already exists
    let existingModal = document.getElementById("customConfirmModal");
    if (existingModal) {
        // if modal exists already, remove it
        existingModal.remove();
    }

    // create modal wrapper
    const modal = document.createElement("div");
    modal.id = "customConfirmModal";
    modal.style.cssText = `
        position: fixed;
        top: 0; left: 0;
        width: 100%; height: 100%;
        background-color: rgba(0,0,0,0.5);
        display: flex; align-items: center; justify-content: center;
        z-index: 1000;
    `;

    // create modal content
    const content = document.createElement("div");
    content.style.cssText = `
        background: white;
        border: 2px solid black;
        padding: 20px;
        text-align: center;
        max-width: 300px;
        box-shadow: 0 2px 10px rgba(0,0,0,0.3);
    `;

    const msg = document.createElement("p");
    msg.textContent = message;

    const yesBtn = document.createElement("button");
    yesBtn.textContent = "Yes";
    yesBtn.style.cssText = "margin: 10px; padding: 8px 16px; background-color: lightgray; color: black; border: 2px solid black; cursor: pointer;";
    yesBtn.onclick = () => {
        modal.remove();
        onConfirm();
    };

    const noBtn = document.createElement("button");
    noBtn.textContent = "Cancel";
    noBtn.style.cssText = "margin: 10px; padding: 8px 16px; background-color: lightgray; color: black; border: 2px solid black; cursor: pointer;";
    noBtn.onclick = () => modal.remove();

    // append everything
    content.appendChild(msg);
    content.appendChild(yesBtn);
    content.appendChild(noBtn);
    modal.appendChild(content);
    document.body.appendChild(modal);
}

document.addEventListener("DOMContentLoaded", function() {
    
    ////////////////////////////////////////////// DISPLAY EXISTING PHARMACY STOCK AVAILABILITIES ////////////////////////////////////////
    
    // on clicking "Manage existing Stock Availability listings"
    document.getElementById("display-availabilities").addEventListener("click", async function(event) {

        // use event.preventDefault to prevent page reload on button click
        event.preventDefault();

        // clear any previous message
        const messageContainer = document.getElementById("message-container");
        messageContainer.innerHTML = "";

        // clear any previous results
        const resultsContainer = document.getElementById("results-container");
        resultsContainer.innerHTML = "";

        // clear welcome message
        document.getElementById("welcome").innerHTML = "";

        // clear buttons
        const buttonsContainer = document.getElementById("buttons-container");
        buttonsContainer.innerHTML = "";
        buttonsContainer.classList.add("form-buttons");

        // create "Back" button
        const backButton = document.createElement("button");
        backButton.textContent = "Back";
        backButton.addEventListener("click", () => {
            window.location.href = "account-dashboard.html";
        });

        // add the button to the container
        buttonsContainer.appendChild(backButton);

        // start from page 0
        let currentPage = 0;

        // function to get availabilities for a specific page
        async function getAvailabilities(page) {
        
            // add necessary query parameters
            const url = new URL("http://localhost:8080/pharmacy-drug-availabilities/view-all");
            url.searchParams.append("page", page); // pagination parameter
            url.searchParams.append("size", 10); // number of results per page

            try {
                // call backend view all pharmacy drug availabilities API
                const response = await fetch(url, {
                    method: "GET",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    credentials: "include"
                });

                // if the response from the API is ok
                if (response.ok && response.status !== 204) {
                    // update the page to display the paginated list of availability listings returned by the API
                    const results = await response.json();
                    displayAvailabilityListings(results);
                } 
                else {
                    messageContainer.style.display = "flex";
                    // handle error responses
                    if(response.status == 204){
                        messageContainer.innerHTML = "You currently have no stock availability listings.";
                    }
                    else{
                        const responseData = await response.json();
                        throw new Error(responseData.error || "An error occurred. Please try again.");
                    }
                }
            } catch (error) {
                // catch error thrown by try block and display it as a message
                console.error("Error:", error);
                messageContainer.style.display = "flex";
                messageContainer.innerHTML = error.message;
            }
        }

        // function to display availability listings in a table
        function displayAvailabilityListings(results) {
            
            // clear previous page of results
            resultsContainer.innerHTML = "";
            
            // display message
            messageContainer.style.display = "flex";
            messageContainer.innerHTML = "Stock availability listings for your pharmacy:";

            // create results table
            const table = document.createElement("table");
            table.classList.add("results-table");

            // create the table headers
            const thead = document.createElement("thead");
            thead.innerHTML = `
                <tr>
                    <th>Product Licence Number</th>
                    <th>Product</th>
                    <th></th>
                </tr>
            `;
            table.appendChild(thead);

            // create the table body
            const tbody = document.createElement("tbody");

            // append each result as a row in the table
            results.content.forEach(item => {
                const row = document.createElement("tr");
                row.innerHTML = `
                    <td>${item.drug.licenceNo}</td>
                    <td>${item.drug.productName}, ${item.drug.dosageForm} - (${item.drug.manufacturer})</td>
                    <td><button class="delete-btn" data-id="${item.id}">Delete</button></td>
                `;
                tbody.appendChild(row);
            });

            // append the table body to the table
            table.appendChild(tbody);
            resultsContainer.appendChild(table);

            // add event listener for delete buttons using event delegation
            table.addEventListener("click", async function (e) {
                // if the element clicked in the table is a delete button
                if (e.target.classList.contains("delete-btn")) {
                    // extract the stock availability id from the delete button
                    const idToDelete = e.target.getAttribute("data-id");
                    // ask the user if they are sure they wish to delete the listing
                    showConfirmModal("Are you sure you want to delete this listing?", async function() {
                        try {
                            const response = await fetch(`http://localhost:8080/pharmacy-drug-availabilities/delete/${idToDelete}`, {
                                method: "DELETE",
                                credentials: "include"
                            });
                            if (response.status === 204) {
                                // refresh the page
                                getAvailabilities(currentPage);
                                setTimeout(() => {
                                    messageContainer.innerHTML += "<br><br>Listing deleted successfully.";
                                }, 60);
                                setTimeout(() => {
                                    messageContainer.innerHTML = "Stock availability listings for your pharmacy:";
                                }, 3000);
                            } else {
                                const responseData = await response.json();
                                throw new Error(responseData.error || "An error occurred. Please try again.");
                            }
                        } catch (error) {
                            console.error("Error:", error);
                            messageContainer.style.display = "flex";
                            messageContainer.innerHTML = error.message;
                        }
                    });
                }
            });

            // pagination controls
            const paginationControls = document.createElement("div");
            paginationControls.classList.add("pagination-controls");

            // add "Previous" button if it's not the first page
            if (!results.first) {
                const prevButton = document.createElement("button");
                prevButton.textContent = "Previous";
                prevButton.onclick = () => {
                    currentPage -= 1;
                    getAvailabilities(currentPage);
                };
                paginationControls.appendChild(prevButton);
            }

            // add "Next" button if it's not the last page
            if (!results.last) {
                const nextButton = document.createElement("button");
                nextButton.textContent = "Next";
                nextButton.onclick = () => {
                    currentPage += 1;
                    getAvailabilities(currentPage);
                };
                paginationControls.appendChild(nextButton);
            }

            // append the pagination controls to the results container
            resultsContainer.appendChild(paginationControls);
        }

        // starting from page 0 as defined above
        getAvailabilities(currentPage);
    });

});
