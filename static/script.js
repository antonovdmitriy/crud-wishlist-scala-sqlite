const apiUrl = "http://localhost:8080/wishlist";

const table = document.getElementById('wishlist-table');
const addItemBtn = document.getElementById('add-item-btn');
const editItemBtn = document.getElementById('edit-item-btn');
const deleteItemBtn = document.getElementById('delete-item-btn');

const itemModal = document.getElementById('item-modal');
const cancelAddItemBtn = document.getElementById('cancel-add-btn');
const itemForm = document.getElementById('item-form');
const refreshItemBtn = document.getElementById('refresh-item-btn');
const closeBtn = document.querySelector('.close'); // Get the close button element

let data;
let currentItem;
let formMode;

editItemBtn.disabled = true
deleteItemBtn.disabled = true
refreshItemBtn.addEventListener('click', () => {
    refreshItems();
});

// Show modal when "Add Item" button is clicked
addItemBtn.addEventListener('click', () => {
    resetModalFields(itemModal);
    formMode = 'add';
    document.getElementById('modal-title').innerHTML = 'Add item'
    itemModal.style.display = 'block';
});

// Show modal when "Edit Item" button is clicked
editItemBtn.addEventListener('click', () => {

    if (currentItem) {
        resetModalFields(itemModal);

        formMode = 'edit';

        document.getElementById('modal-title').innerHTML = 'Edit item'
        document.getElementById('item-name').value = currentItem.name;
        document.getElementById('item-price').value = currentItem.price;
        document.getElementById('item-link-to-order').value = currentItem.linkToOrder;
        document.getElementById('item-description').value = currentItem.description;

        itemModal.style.display = 'block';
    }
});

deleteItemBtn.addEventListener('click', async (event) => {
    event.preventDefault();
    if (currentItem) {
        try {
            // Send POST request to API endpoint
            const response = await fetch(`${apiUrl}/${currentItem.id}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            if (response.ok) {
                refreshItems();
            } else {
                alert('Failed to add item. Please try again.');
            }
        } catch (error) {
            console.error('Error:', error);
        }
    }
});



// Close modal when "Cancel" button is clicked
cancelAddItemBtn.addEventListener('click', () => {
    itemModal.style.display = 'none';
    formMode = undefined;
});

// Close modal when "close" button is clicked
closeBtn.addEventListener('click', () => {
    itemModal.style.display = 'none';
    formMode = undefined;
});

function resetModalFields(element) {
    // Get all input fields within the specified element
    const inputFields = element.querySelectorAll("input, textarea");

    // Loop through each input field and reset its value to empty
    inputFields.forEach(input => {
        input.value = "";
    });
}

// Handle form submission for adding item
itemForm.addEventListener('submit', async (event) => {
    event.preventDefault();

    // Get form input values
    const itemName = document.getElementById('item-name').value;
    const itemPrice = parseFloat(document.getElementById('item-price').value);
    const itemLinkToOrder = document.getElementById('item-link-to-order').value;
    const itemDescription = document.getElementById('item-description').value;

    // Prepare JSON data
    const data = {
        name: itemName,
        price: itemPrice,
        linkToOrder: itemLinkToOrder,
        description: itemDescription
    };


    if (formMode == 'add') {

        try {
            // Send POST request to API endpoint
            const response = await fetch(apiUrl, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            });

            if (response.ok) {
                // If successful, close modal and refresh table
                itemModal.style.display = 'none';
                formMode = undefined;
                refreshItems();
            } else {
                // If unsuccessful, display error message
                alert('Failed to add item. Please try again.');
            }
        } catch (error) {
            console.error('Error:', error);
        }
    }

    if (formMode == 'edit') {

        try {
            // Send PUT request to API endpoint
            const response = await fetch(`${apiUrl}/${currentItem.id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            });

            if (response.ok) {
                // If successful, close modal and refresh table
                itemModal.style.display = 'none';
                formMode = undefined;
                refreshItems();
            } else {
                // If unsuccessful, display error message
                alert('Failed to add item. Please try again.');
            }
        } catch (error) {
            console.error('Error:', error);
        }
    }

});


// Function to refresh items
function refreshItems() {
    fetch(apiUrl)
        .then(response => response.json())
        .then(items => {
            data = items; // Assign data to the variable
            currentItem = undefined;
            // Update table
            const table = document.getElementById("wishlist-table");
            table.innerHTML = `
                <tr>
                    <th>Id</th>
                    <th>Name</th>
                    <th>Price</th>
                </tr>
            `;
            data.forEach(item => {
                const row = document.createElement("tr");
                row.innerHTML = `
                    <td>${item.id}</td>
                    <td>${item.name}</td>
                    <td>${item.price}</td>
                `;
                table.appendChild(row);
            });

            // Update text areas
            const linkToOrderTextArea = document.getElementById("link-to-order");
            const descriptionTextArea = document.getElementById("description");
            if (data.length > 0) {
                linkToOrderTextArea.value = data[0].linkToOrder || '';
                descriptionTextArea.value = data[0].description || '';
            } else {
                linkToOrderTextArea.value = '';
                descriptionTextArea.value = '';
            }

            // Remove previous event listener before adding a new one
            table.removeEventListener('click', handleTableRowClick);
            table.addEventListener('click', handleTableRowClick);
        })
        .catch(error => {
            console.error("Error fetching wishlist items:", error);
        });
}

// Function to handle table row click event
function handleTableRowClick(e) {
    const target = e.target;
    // Check if clicked element is a table row
    if (target.tagName === 'TD' && target.parentNode.tagName === 'TR') {


        editItemBtn.disabled = false
        deleteItemBtn.disabled = false

        // Get data from clicked row
        const itemId = parseInt(target.parentNode.cells[0].textContent); // Convert itemId to a number

        currentItem = data.find(item => item.id === itemId)

        const itemLinkToOrder = currentItem.linkToOrder || ''; // Use optional chaining to handle undefined
        const itemDescription = currentItem.description || ''; // Use optional chaining to handle undefined

        // Set data to textareas
        const linkToOrderTextArea = document.getElementById("link-to-order");
        const descriptionTextArea = document.getElementById("description");
        linkToOrderTextArea.value = itemLinkToOrder;
        descriptionTextArea.value = itemDescription;

        // Remove selected class from previously selected row
        const selectedRow = document.querySelector(".selected");
        if (selectedRow) {
            selectedRow.classList.remove("selected");
        }

        // Add selected class to clicked row
        target.parentNode.classList.add("selected");
    }
}

// Call refreshItems() on page load
window.addEventListener('load', refreshItems);



