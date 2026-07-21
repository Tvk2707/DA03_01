<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="modal" id="customerModal" style="display:none;">
    <div class="modal-content" style="max-width: 500px;">
        <span class="close-btn" id="closeCustomerModal" onclick="this.parentElement.parentElement.style.display='none'">&times;</span>
        <h2>Chọn hoặc tạo khách hàng</h2>
        <div class="form-group">
            <label for="customerSearchInput">Tìm theo SĐT</label>
            <input type="text" id="customerSearchInput" class="filter-input" placeholder="Nhập số điện thoại..." oninput="traCuuKhachHang(this.value)">
        </div>
        <div id="customerSearchResults">
            <%-- Search results will be displayed here --%>
        </div>
        <div id="createCustomerForm" style="display:none; margin-top: 16px;">
            <h4>Tạo khách hàng mới</h4>
            <div class="form-group">
                <label for="newCustomerName">Họ và tên</label>
                <input type="text" id="newCustomerName" class="filter-input" placeholder="Nhập họ tên khách hàng...">
            </div>
            <button id="createCustomerBtn" class="add-new-btn" onclick="traCuuKhachHang(document.getElementById('customerSearchInput').value, document.getElementById('newCustomerName').value)">Tạo và chọn</button>
        </div>
    </div>
</div>
