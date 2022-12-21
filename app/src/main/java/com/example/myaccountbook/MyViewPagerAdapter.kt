package com.example.myaccountbook

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

// 수업 시간 코드와 달리(viewpageradapter을 activity에 달음)
//*** !!! 매개변수를 activity 타입이 아니라 fragment 타입으로 받아야함!!! ***
class MyViewPagerAdapter(fragmentView: Fragment) :
    FragmentStateAdapter(fragmentView) {
    override fun getItemCount(): Int {
        //페이지 2개 만들거
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        //fragment 만들어서 반환
        return when (position) {
            0 -> CostFragment_bc()
            1 -> IncomeFragment_bc()
            else -> CostFragment_bc()
        }
    }

}