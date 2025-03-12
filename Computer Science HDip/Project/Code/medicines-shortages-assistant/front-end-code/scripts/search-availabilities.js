document.addEventListener("DOMContentLoaded", function() {
    
    ////////////////////////////////////////////// SEARCH FOR A PRODUCT AVAILABILITY ////////////////////////////////////////
    
    // on submission of option (clicking "Search")
    document.getElementById("search-availabilities").addEventListener("submit", async function(event) {

        // use event.preventDefault to prevent page reload on form submission
        event.preventDefault(); 

        // clear any previous message
        const messageContainer = document.getElementById("message-container");
        messageContainer.innerHTML = "";

        // clear any previous results
        const resultsContainer = document.getElementById("results-container");
        resultsContainer.innerHTML = "";
        
        // get the value submitted (drug licenceNo)
        const licenceNo = document.getElementById("shorts").value;
        // start from page 0
        let currentPage = 0;

        async function getAvailabilities(page) {
        
            // add necessary query parameters
            const url = new URL("http://localhost:8080/pharmacy-drug-availabilities/search-for-stock");
            url.searchParams.append("licenceNo", licenceNo);
            // pagination parameter
            url.searchParams.append("page", page);
            // number of results per page
            url.searchParams.append("size", 10);

            try {

                // call backend search for stock pharmacy drug availability API
                const response = await fetch(url, {
                    method: "GET",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    credentials: "include"
                });


                // if the response from the API is ok
                if (response.ok) {
                    // update the page to display the paginated list of availability listings returned by the API
                    const results = await response.json();
                    displayAvailabilityListings(results);
                } 

                else {
                    messageContainer.style.display = "flex";
                    // extract the http status from the create availability API and throw it as a new error
                    // or if it is an undefined error, display generic error message
                    if(response.status == 404){
                        messageContainer.innerHTML = "No stock availability listings found for the selected product.";
                    }
                    else{
                        const responseData = await response.json();
                        throw new Error(responseData.error || "An error occurred. Please try again.");
                    }
                }

            } catch (error) {
                // catch error thrown by try block and display it as an alert
                console.error("Error:", error);
                messageContainer.style.display = "flex";
                messageContainer.innerHTML = error.message;
            }
        }
        
        function displayAvailabilityListings(results) {
            
            // clear previous page of results
            resultsContainer.innerHTML = "";
            
            // create results table
            const table = document.createElement("table");
            table.classList.add("results-table");

            // create the table headers
            const thead = document.createElement("thead");
            thead.innerHTML = `
                <tr>
                    <th>Pharmacy</th>
                    <th>Address</th>
                    <th>Contact Number</th>
                    <th>Product</th>
                    <th>Availability</th>
                </tr>
            `;
            // append headers to the table
            table.appendChild(thead);

            // create the table body
            const tbody = document.createElement("tbody");

            // append each result as a row in the table
            results.content.forEach(item => {
                const row = document.createElement("tr");
                row.innerHTML = `
                    <td>${item.pharmacy.pharmacyName}</td>
                    <td>${item.pharmacy.address}, ${item.pharmacy.eircode}</td>
                    <td>${item.pharmacy.phoneNo}</td>
                    <td>${item.drug.productName}, ${item.drug.dosageForm} - (${item.drug.manufacturer})</td>
                    <td>${item.isAvailable ? "Available" : "Out of Stock"}</td>
                `;
                tbody.appendChild(row);
            });

            // append the table body to the table
            table.appendChild(tbody);
            resultsContainer.appendChild(table);

            // pagination controls
            const paginationControls = document.createElement("div");
            paginationControls.classList.add("pagination-controls");
            
            // if it isn't the first page of results, append a "Previous" button to allow for navigation to previous pages
            if (!results.first) {
                const prevButton = document.createElement("button");
                prevButton.textContent = "Previous";
                prevButton.onclick = () => {
                    currentPage -= 1;
                    getAvailabilities(currentPage);
                };
                paginationControls.appendChild(prevButton);
            }
            
            // similarly, if it isn't the last page, append a "Next" button to allow for navigation to subsequent pages
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