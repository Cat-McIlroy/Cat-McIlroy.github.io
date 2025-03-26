document.addEventListener("DOMContentLoaded", function() {
    
    ////////////////////////////////////////////// SEARCH BY ACTIVE INGREDIENT ////////////////////////////////////////
    
    // on submission of value (clicking "Search")
    document.getElementById("active-ingredient-search").addEventListener("submit", async function(event) {

        // use event.preventDefault to prevent page reload on form submission
        event.preventDefault(); 

        // clear any previous message
        const messageContainer = document.getElementById("message-container");
        messageContainer.innerHTML = "";

        // clear any previous results
        const resultsContainer = document.getElementById("results-container");
        resultsContainer.innerHTML = "";
        
        // get the value submitted (the active ingredient)
        const activeIngredient = document.getElementById("active-ingredient").value;
        // start from page 0
        let currentPage = 0;

        async function getDrugsByActiveIngredient(page) {
        
            // add necessary query parameters
            const url = new URL("http://localhost:8080/drugs/search-by-active-substance");
            url.searchParams.append("activeSubstance", activeIngredient);
            // pagination parameter
            url.searchParams.append("page", page);
            // number of results per page
            url.searchParams.append("size", 10);

            try {

                // call backend search for drugs by active ingredient API
                const response = await fetch(url, {
                    method: "GET",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    credentials: "include"
                });


                // if the response from the API is ok
                if (response.ok) {
                    // update the page to display the paginated list of drugs returned by the API
                    const results = await response.json();
                    displayDrugs(results);
                } 

                else {
                    messageContainer.style.display = "flex";
                    // extract the http status from the create availability API and throw it as a new error
                    // or if it is an undefined error, display generic error message
                    if(response.status == 404){
                        messageContainer.innerHTML = "No alternative medications found with the specified active ingredient(s).";
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
        
        function displayDrugs(results) {
            
            // clear previous page of results
            resultsContainer.innerHTML = "";
            
            // create results table
            const table = document.createElement("table");
            table.classList.add("results-table");

            // create the table headers
            const thead = document.createElement("thead");
            thead.innerHTML = `
                <tr>
                    <th>Product Licence</th>
                    <th>Product Name</th>
                    <th>Manufacturer</th>
                    <th>Dosage Form</th>
                    <th>Active Substance</th>
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
                    <td>${item.licenceNo}</td>
                    <td>${item.productName}</td>
                    <td>${item.manufacturer}</td>
                    <td>${item.dosageForm}</td>
                    <td>${item.activeSubstance}</td>
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
                    getDrugsByActiveIngredient(currentPage);
                };
                paginationControls.appendChild(prevButton);
            }
            
            // similarly, if it isn't the last page, append a "Next" button to allow for navigation to subsequent pages
            if (!results.last) {
                const nextButton = document.createElement("button");
                nextButton.textContent = "Next";
                nextButton.onclick = () => {
                    currentPage += 1;
                    getDrugsByActiveIngredient(currentPage);
                };
                paginationControls.appendChild(nextButton);
            }

            // append the pagination controls to the results container
            resultsContainer.appendChild(paginationControls);
        }
        
        // starting from page 0 as defined above
        getDrugsByActiveIngredient(currentPage);
        
    });
    
});