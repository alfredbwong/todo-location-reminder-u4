package com.udacity.project4.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.databinding.FragmentLoginBinding
import com.udacity.project4.locationreminders.reminderslist.LoginViewModel
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import org.koin.android.ext.android.inject


class LoginFragment : BaseFragment() {
    override val _viewModel: LoginViewModel by inject()

    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}