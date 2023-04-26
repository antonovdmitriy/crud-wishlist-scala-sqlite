const apiUrl = "/wishlist";

const table = document.getElementById("wishlist-table");
const addItemBtn = document.getElementById("add-item-btn");
const editItemBtn = document.getElementById("edit-item-btn");
const deleteItemBtn = document.getElementById("delete-item-btn");

const itemModal = document.getElementById("item-modal");
const cancelAddItemBtn = document.getElementById("cancel-add-btn");
const itemForm = document.getElementById("item-form");
const refreshItemBtn = document.getElementById("refresh-item-btn");
const closeBtn = document.querySelector(".close");

let data;
let currentItem;
let formMode;

editItemBtn.disabled = true;
deleteItemBtn.disabled = true;

const themeSwitch = document.getElementById("switch");

function toggleTheme() {
  if (themeSwitch.checked) {
    document.body.classList.add("dark");
    document.body.classList.remove("light");
  } else {
    document.body.classList.remove("dark");
    document.body.classList.add("light");
  }
}

themeSwitch.addEventListener("change", toggleTheme);

toggleTheme();

refreshItemBtn.addEventListener("click", () => {
  refreshItems();
});

addItemBtn.addEventListener("click", () => {
  resetModalFields(itemModal);
  formMode = "add";
  document.getElementById("modal-title").innerHTML = "Add item";
  itemModal.style.display = "block";
});

editItemBtn.addEventListener("click", () => {
  if (currentItem) {
    resetModalFields(itemModal);

    formMode = "edit";

    document.getElementById("modal-title").innerHTML = "Edit item";
    document.getElementById("item-name").value = currentItem.name;
    document.getElementById("item-price").value = currentItem.price;
    document.getElementById("item-link-to-order").value =
      currentItem.linkToOrder;
    document.getElementById("item-description").value = currentItem.description;

    itemModal.style.display = "block";
  }
});

deleteItemBtn.addEventListener("click", async (event) => {
  event.preventDefault();
  if (currentItem) {
    try {
      const response = await fetch(`${apiUrl}/${currentItem.id}`, {
        method: "DELETE",
        headers: {
          "Content-Type": "application/json",
        },
      });

      if (response.ok) {
        refreshItems();
      } else {
        alert("Failed to add item. Please try again.");
      }
    } catch (error) {
      console.error("Error:", error);
    }
  }
});

cancelAddItemBtn.addEventListener("click", () => {
  itemModal.style.display = "none";
  formMode = undefined;
});

closeBtn.addEventListener("click", () => {
  itemModal.style.display = "none";
  formMode = undefined;
});

function resetModalFields(element) {
  const inputFields = element.querySelectorAll("input, textarea");

  inputFields.forEach((input) => {
    input.value = "";
  });
}

itemForm.addEventListener("submit", async (event) => {
  event.preventDefault();

  const itemName = document.getElementById("item-name").value;
  const itemPrice = parseFloat(document.getElementById("item-price").value);
  const itemLinkToOrder = document.getElementById("item-link-to-order").value;
  const itemDescription = document.getElementById("item-description").value;

  const data = {
    name: itemName,
    price: itemPrice,
    linkToOrder: itemLinkToOrder,
    description: itemDescription,
  };

  let apiUrlWithId = apiUrl;
  let method = "POST";
  if (formMode === "edit") {
    apiUrlWithId += `/${currentItem.id}`;
    method = "PUT";
  }

  try {
    const response = await fetch(apiUrlWithId, {
      method,
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(data),
    });

    if (response.ok) {
      itemModal.style.display = "none";
      formMode = undefined;
      refreshItems();
    } else {
      alert("Failed to add/edit item. Please try again.");
    }
  } catch (error) {
    console.error("Error:", error);
  }
});

function refreshItems() {
  fetch(apiUrl)
    .then((response) => response.json())
    .then((items) => {
      data = items;
      currentItem = undefined;
      const table = document.getElementById("wishlist-table");
      table.innerHTML = `
                <tr>
                    <th>Id</th>
                    <th>Name</th>
                    <th>Price</th>
                </tr>
            `;
      data.forEach((item) => {
        const row = document.createElement("tr");
        row.innerHTML = `
                    <td>${item.id}</td>
                    <td>${item.name}</td>
                    <td>${item.price}</td>
                `;
        table.appendChild(row);
      });

      const linkToOrderTextArea = document.getElementById("link-to-order");
      const descriptionTextArea = document.getElementById("description");
      if (data.length > 0) {
        linkToOrderTextArea.value = data[0].linkToOrder || "";
        descriptionTextArea.value = data[0].description || "";
      } else {
        linkToOrderTextArea.value = "";
        descriptionTextArea.value = "";
      }

      table.removeEventListener("click", handleTableRowClick);
      table.addEventListener("click", handleTableRowClick);
    })
    .catch((error) => {
      console.error("Error fetching wishlist items:", error);
    });
}

function handleTableRowClick(e) {
  const target = e.target;
  if (target.tagName === "TD" && target.parentNode.tagName === "TR") {
    editItemBtn.disabled = false;
    deleteItemBtn.disabled = false;

    const itemId = parseInt(target.parentNode.cells[0].textContent);

    currentItem = data.find((item) => item.id === itemId);

    const itemLinkToOrder = currentItem.linkToOrder || "";
    const itemDescription = currentItem.description || "";

    const linkToOrderTextArea = document.getElementById("link-to-order");
    const descriptionTextArea = document.getElementById("description");
    linkToOrderTextArea.value = itemLinkToOrder;
    descriptionTextArea.value = itemDescription;

    const selectedRow = document.querySelector(".selected");
    if (selectedRow) {
      selectedRow.classList.remove("selected");
    }

    target.parentNode.classList.add("selected");
  }
}

window.addEventListener("load", refreshItems);
