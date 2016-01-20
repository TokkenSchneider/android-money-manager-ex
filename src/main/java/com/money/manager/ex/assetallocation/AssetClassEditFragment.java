/*
 * Copyright (C) 2012-2016 The Android Money Manager Ex Project Team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.money.manager.ex.assetallocation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.money.manager.ex.R;
import com.money.manager.ex.common.InputAmountDialog;
import com.money.manager.ex.common.events.AmountEnteredEvent;
import com.money.manager.ex.domainmodel.AssetClass;
import com.money.manager.ex.servicelayer.AssetAllocationService;

import de.greenrobot.event.EventBus;
import info.javaperformance.money.Money;

/**
 * A placeholder fragment containing a simple view.
 */
public class AssetClassEditFragment
    extends Fragment {

    public static final int INPUT_ALLOCATION = 1;
    public static final int CONTEXT_MENU_DELETE = 1;

    public AssetClassEditFragment() {
    }

    public AssetClass assetClass;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_asset_class_edit, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (this.assetClass == null) {
            this.assetClass = AssetClass.create("");
        }

        View view = getView();
        initializeParentEdit(view);
        initializeNameEdit(view);
        initializeAllocationPicker(view);
        // todo: show sort order value

//        initializeFloatingActionButton(view);

//        mAdapter = createAdapter();
//        ListView listView = initializeListView(mAdapter);

//        registerForContextMenu(listView);

        // setListShown(false);
//        Integer id = this.assetClass.getId();
//        if (id != null) {
//            loadData();
//        }
    }

    @Override
    public void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);

        super.onStop();
    }

    // Context menu

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, CONTEXT_MENU_DELETE, 0, R.string.delete);
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
//        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        boolean result = false;

        switch (item.getItemId()) {
//            case CONTEXT_MENU_DELETE:
//                // Delete
//                ListView listView = getListView();
//                if (listView == null) return false;
//
//                Cursor cursor = (Cursor) listView.getItemAtPosition(info.position);
//                Stock stock = Stock.fromCursor(cursor);
//                String stockSymbol = stock.getSymbol();
//
//                AssetClassStockRepository repo = new AssetClassStockRepository(getActivity());
//                int assetClassId = this.assetClass.getId();
//                boolean deleted = repo.delete(assetClassId, stockSymbol);
//                if (!deleted) {
//                    ExceptionHandler handler = new ExceptionHandler(getActivity(), this);
//                    handler.showMessage(getString(R.string.error));
//                }
//
//                result = true;
//                break;
        }
        return result;
    }

    // Events

    public void onEvent(AmountEnteredEvent event) {
        int id = Integer.parseInt(event.requestId);
        switch (id) {
            case INPUT_ALLOCATION:
                assetClass.setAllocation(event.amount);
                updateAllocation();
                break;
        }
    }

    // Private

    private void initializeNameEdit(View view) {
        final EditText edit = (EditText) view.findViewById(R.id.nameEdit);
        if (edit == null) return;

        edit.setText(assetClass.getName());

        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // edit.getText().toString()
                String newValue = s.toString();
                assetClass.setName(newValue);
            }
        });
    }

    private void initializeAllocationPicker(View view) {
        TextView textView = (TextView) view.findViewById(R.id.allocationEdit);
        if (textView == null) return;

        textView.setText(assetClass.getAllocation().toString());

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputAmountDialog dialog = InputAmountDialog.getInstance(INPUT_ALLOCATION,
                    assetClass.getAllocation());
                dialog.setTargetFragment(AssetClassEditFragment.this, INPUT_ALLOCATION);
                dialog.show(getActivity().getSupportFragmentManager(), dialog.getClass().getSimpleName());
            }
        });
    }

    private void initializeParentEdit(View view) {
        TextView edit = (TextView) view.findViewById(R.id.parentAssetClass);
        if (edit == null) return;

        String name;

        if (assetClass.getParentId() == null) {
            name = getString(R.string.none);
            edit.setText(name);
            return;
        }

        AssetAllocationService service = new AssetAllocationService(getActivity());
        name = service.loadName(assetClass.getParentId());
        edit.setText(name);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo: show asset allocation selector.
            }
        };
        // allow changing parent only on existing items

        if (getActivity().getIntent().getAction().equals(Intent.ACTION_EDIT)) {
            edit.setOnClickListener(onClickListener);
        }
    }

    private void updateAllocation() {
        View view = getView();
        if (view == null) return;

        TextView textView = (TextView) view.findViewById(R.id.allocationEdit);
        if (textView != null) {
            Money allocation = assetClass.getAllocation();
            //FormatUtilities.formatAmountTextView();
            textView.setText(allocation.toString());
            textView.setTag(allocation.toString());
        }
    }

//    private void initializeFloatingActionButton(View view) {
//        // attach fab
//        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
//
//        if (this.assetClass.getId() == null) {
//            // new record
//            return;
//        }
//
//        // otherwise we're in edit mode.
//
//        ListView listView = (ListView) view.findViewById(R.id.securitiesList);
//        fab.attachToListView(listView);
//
//        fab.setVisibility(View.VISIBLE);
//
//        View.OnClickListener listener = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // select a security
//                pickStock();
//            }
//        };
//        fab.setOnClickListener(listener);
//    }

//    private void pickStock() {
//        Intent intent = new Intent(getActivity(), SecurityListActivity.class);
//        intent.setAction(Intent.ACTION_PICK);
//        // send the list of existing stock ids to filter out.
//        getActivity().startActivityForResult(intent, AssetClassEditActivity.REQUEST_STOCK_ID);
//    }

//    private MoneySimpleCursorAdapter createAdapter() {
//        return new MoneySimpleCursorAdapter(getActivity(),
//            android.R.layout.simple_list_item_1,
//            null,
//            new String[]{Stock.SYMBOL },
//            new int[]{ android.R.id.text1}, 0);
//
////        setListAdapter(mAdapter);
////        setListShown(false);
//
//    }

//    private ListView getListView() {
//        View view = getView();
//        if (view == null) return null;
//
//        ListView listView = (ListView) view.findViewById(R.id.securitiesList);
//        return listView;
//    }

//    private ListView initializeListView(ListAdapter adapter) {
//        ListView listView = getListView();
//        if (listView == null) return null;
//
//        listView.setAdapter(adapter);
//
//        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                // show context menu
//                getActivity().openContextMenu(view);
//            }
//        };
//        listView.setOnItemClickListener(onItemClickListener);
//
//        return listView;
//    }

}
